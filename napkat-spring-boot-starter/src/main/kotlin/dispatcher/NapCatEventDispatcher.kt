package io.github.xuankaicat.napkat.spring.boot.starter.dispatcher

import io.github.xuankaicat.napkat.core.bot.WebSocketBot
import io.github.xuankaicat.napkat.core.event.*
import io.github.xuankaicat.napkat.spring.boot.starter.annotation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.getBeansWithAnnotation
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.*

/**
 * NapCat 事件分发器
 *
 * 负责监听 Spring 容器启动事件，扫描带有 [NapCatController] 注解的 Bean。
 * 解析其中的事件处理函数（标注了 @EventMapper 元注解的注解），注册到 [WebSocketBot] 中。
 * 当收到 WebSocket 消息时，根据匹配规则调用相应的处理函数。
 *
 * 支持多注解组合逻辑：
 * 1. 默认情况下，函数上标注的多个事件注解之间为 **AND** 关系（必须全部匹配）。
 * 2. 如果需要 OR 关系，可以将多个事件处理逻辑拆分到不同的函数中，或者未来引入 @Or 注解。
 *
 * @property context Spring 应用上下文
 * @property bot WebSocket 机器人实例
 */
@Component
class NapCatEventDispatcher(
    private val context: ApplicationContext,
    private val bot: WebSocketBot
) : NapCatEventListener, ApplicationListener<ContextRefreshedEvent> {

    private val logger = LoggerFactory.getLogger(NapCatEventDispatcher::class.java)
    private val handlers = mutableListOf<HandlerInfo>()
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * 处理器信息内部类
     *
     * @property bean 处理器所在的 Bean 实例
     * @property function 处理函数
     * @property eventType 关注的事件类型
     * @property regex 正则表达式（如果有，仅在单一注解时有效）
     * @property condition 匹配条件函数
     * @property prefixes 控制器前缀
     */
    data class HandlerInfo(
        val bean: Any,
        val function: KFunction<*>,
        val eventType: Class<out NapCatEvent>,
        val regex: String? = null,
        val condition: (NapCatEvent) -> Boolean,
        val prefixes: Array<String> = emptyArray(),
        val startsWith: Array<String> = emptyArray()
    )

    /**
     * Spring 容器启动完成后触发
     * 扫描注解并注册处理器
     */
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        val beans = context.getBeansWithAnnotation<NapCatController>()
        for (bean in beans.values) {
            val kClass = bean::class
            val controllerAnno = kClass.findAnnotation<NapCatController>()
            val prefixes = controllerAnno?.prefix ?: emptyArray()

            for (func in kClass.declaredFunctions) {
                // 1. 获取函数上直接标注的所有 @EventMapper 注解
                val annotations = func.annotations.filter { 
                    it.annotationClass.findAnnotation<EventMapper>() != null 
                }
                
                if (annotations.isEmpty()) continue

                // 2. 解析所有注解，构建组合逻辑
                // 策略改为：AND 关系。函数上所有注解都必须匹配。
                // 这允许组合多个条件（如 @OnGroupMessage + @OnMessage(regex="...")）。
                val processors = mutableListOf<Triple<Class<out NapCatEvent>, (NapCatEvent) -> Boolean, AnnotationInfo>>()

                for (anno in annotations) {
                    val result = parseEventMapperAnnotation(anno)
                    if (result != null) {
                        processors.add(result)
                    }
                }

                if (processors.isNotEmpty()) {
                    // 确定组合后的 EventType
                    // 在 AND 模式下，事件类型必须兼容。通常取最具体的子类，或者如果不兼容则为 Nothing (无法触发)。
                    // 简单起见，如果所有处理器关注同一类型，则用该类型；否则用 NapCatEvent，在 condition 中具体判断。
                    val commonEventType = if (processors.size == 1) {
                        processors[0].first
                    } else {
                        // 尝试找到最具体的公共类型？
                        // 实际上，如果一个是 GroupMessageEvent，一个是 MessageEvent，交集是 GroupMessageEvent。
                        // 这里简化处理，如果全一样则用它，否则用基类，反正 condition 会检查类型。
                        val firstType = processors[0].first
                        if (processors.all { it.first == firstType }) firstType else NapCatEvent::class.java
                    }

                    // 聚合 Regex (取第一个非空的)
                    val combinedRegex = processors.firstNotNullOfOrNull { it.third.regex }
                    
                    // 聚合 startsWith (合并所有 startsWith)
                    // 注意：如果是 AND 关系，startsWith 必须都满足？
                    // 不，AnnotationInfo 里的 startsWith 是注解参数。
                    // 多个注解都有 startsWith 时，应该取并集还是交集？
                    // 实际上，通常只有一个注解会有 startsWith。如果有多个，我们简单合并。
                    val allStartsWith = processors.flatMap { it.third.startsWith.toList() }.toTypedArray()

                    // 构建组合条件 (AND)
                    val combinedCondition: (NapCatEvent) -> Boolean = { e ->
                        processors.all { (type, cond, _) -> 
                            type.isInstance(e) && cond(e) 
                        }
                    }

                    handlers.add(HandlerInfo(bean, func, commonEventType, combinedRegex, combinedCondition, prefixes, allStartsWith))
                    logger.debug("Registered handler for ${func.name} with ${processors.size} annotations (Mode=AND)")
                }
            }
        }
        
        bot.registerListener(this)
        logger.info("Registered ${handlers.size} NapCat event handlers")
    }

    data class AnnotationInfo(
        val regex: String?,
        val startsWith: Array<String>
    )

    /**
     * 解析单个 EventMapper 注解
     */
    private fun parseEventMapperAnnotation(anno: Annotation): Triple<Class<out NapCatEvent>, (NapCatEvent) -> Boolean, AnnotationInfo>? {
        val eventMapper = anno.annotationClass.findAnnotation<EventMapper>() ?: return null
        try {
            val processorClass = eventMapper.processor
            val processor = processorClass.objectInstance ?: processorClass.createInstance()
            @Suppress("UNCHECKED_CAST")
            val typedProcessor = processor as EventProcessor<Annotation>
            
            val eventType = typedProcessor.getEventType()
            val condition = typedProcessor.resolve(anno)
            
            var regex: String? = null
            var startsWith: Array<String> = emptyArray()

            try {
                val regexProp = anno.annotationClass.members.find { it.name == "regex" }
                if (regexProp != null) {
                    regex = regexProp.call(anno) as? String
                }
            } catch (e: Exception) { }

            try {
                val startsWithProp = anno.annotationClass.members.find { it.name == "startsWith" }
                if (startsWithProp != null) {
                    @Suppress("UNCHECKED_CAST")
                    startsWith = startsWithProp.call(anno) as? Array<String> ?: emptyArray()
                }
            } catch (e: Exception) { }

            // 包装 condition：先检查类型
            val safeCondition: (NapCatEvent) -> Boolean = { e ->
                eventType.isInstance(e) && condition(e)
            }

            return Triple(eventType, safeCondition, AnnotationInfo(regex, startsWith))
        } catch (e: Exception) {
            logger.error("Failed to parse annotation ${anno.annotationClass.simpleName}", e)
            return null
        }
    }

    /**
     * 收到 WebSocket 事件回调
     *
     * @param event NapCat 事件对象
     */
    override suspend fun onEvent(event: NapCatEvent) {
        scope.launch {
            handlers.forEach { handler ->
                // 使用 handler.condition 检查（这里可能已经包含了前缀匹配逻辑，但我们无法获取 matchedPrefix）
                // 问题：handler.condition 是闭包，它知道是否匹配，但这里我们不知道匹配了哪个前缀。
                // 如果 condition 返回 true，说明前缀匹配成功（如果有），且后续注解也匹配成功。
                // 关键点：我们在 condition 闭包中构造了 effectiveEvent (去除了前缀)，但这个对象在闭包返回 true 后就丢弃了。
                // resolveArgs 使用的是原始 event，这意味着注入的 MsgBody 仍然包含前缀！
                // 这是一个设计问题。
                
                // 解决方案：
                // 将前缀匹配逻辑提取出来，在 onEvent 中显式处理。
                
                var effectiveEvent = event
                if (handler.prefixes.isNotEmpty() && event is MessageEvent) {
                    var matchedPrefix = ""
                    for (prefix in handler.prefixes) {
                        if (event.rawMessage.startsWith(prefix)) {
                            matchedPrefix = prefix
                            break
                        }
                    }
                    
                    if (matchedPrefix.isEmpty()) return@forEach // 前缀不匹配，跳过此 handler
                    
                    // 构造去除前缀的事件对象
                     effectiveEvent = when (event) {
                        is GroupMessageEvent -> event.copy(rawMessage = event.rawMessage.removePrefix(matchedPrefix))
                        is PrivateMessageEvent -> event.copy(rawMessage = event.rawMessage.removePrefix(matchedPrefix))
                        else -> event
                    }
                }
                
                // 使用 effectiveEvent 进行后续匹配 (Processors)
                // 注意：handler.condition 之前的实现包含了前缀检查，我们需要移除它，或者在这里重新实现。
                // 为了避免重复检查，我们修改 handler.condition 的构造逻辑，不再包含前缀检查。
                // 见下文修改 onApplicationEvent 中的 combinedCondition。
                
                try {
                    if (handler.eventType.isInstance(effectiveEvent) && handler.condition(effectiveEvent)) {
                        val args = resolveArgs(handler, effectiveEvent)
                        if (handler.function.isSuspend) {
                            handler.function.callSuspend(*args)
                        } else {
                            handler.function.call(*args)
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Error invoking handler ${handler.function.name}", e)
                }
            }
        }
    }

    /**
     * 解析参数
     * 根据参数类型或注解，注入相应的值
     */
    private fun resolveArgs(handler: HandlerInfo, event: NapCatEvent): Array<Any?> {
        return handler.function.parameters.map { param ->
            if (param.kind == KParameter.Kind.INSTANCE) {
                return@map handler.bean
            }
            
            val type = param.type.classifier
            
            // 1. Inject Event itself
            if (type == event::class || type == NapCatEvent::class) {
                return@map event
            }
            if (event::class.isInstance(type) || (type is Class<*> && type.isInstance(event))) {
                 return@map event
            }
            // Check specific types manually if needed, but isInstance above should cover it if classifier is KClass
            if (event is GroupMessageEvent && type == GroupMessageEvent::class) return@map event
            if (event is PrivateMessageEvent && type == PrivateMessageEvent::class) return@map event
            if (event is MessageEvent && type == MessageEvent::class) return@map event
            
            // 2. Inject Bot
            if (type == WebSocketBot::class) {
                return@map bot
            }
            
            // 3. Annotation Resolvers
            param.findAnnotation<MsgBody>()?.let {
                if (event is MessageEvent) return@map event.rawMessage
            }
            
            param.findAnnotation<SenderId>()?.let {
                if (event is MessageEvent) return@map event.userId
                if (event is RequestEvent) return@map event.userId
                if (event is NoticeEvent) return@map event.userId ?: 0L
            }
            
            param.findAnnotation<GroupId>()?.let {
                if (event is GroupMessageEvent) return@map event.groupId
                if (event is NoticeEvent) return@map event.groupId ?: 0L
                if (event is RequestEvent) return@map event.groupId ?: 0L
            }

            param.findAnnotation<EventBody>()?.let {
                return@map event
            }
            
            param.findAnnotation<MatchResult>()?.let {
                // 如果有 regex，尝试再次匹配以获取 MatchResult
                if (event is MessageEvent && handler.regex != null && handler.regex.isNotEmpty()) {
                    val match = Regex(handler.regex).find(event.rawMessage)
                    if (type == kotlin.text.MatchResult::class) return@map match
                    return@map match
                }
            }

            param.findAnnotation<Param>()?.let {
                if (event is MessageEvent) {
                    var raw = event.rawMessage.trim()
                    // 0. 去除 startsWith (如果匹配)
                    // 使用 handler.startsWith (来自注解参数)
                    // 如果有多个 startsWith，这里会去除匹配的那个。
                    if (handler.startsWith.isNotEmpty()) {
                        for (prefix in handler.startsWith) {
                            if (raw.startsWith(prefix)) {
                                raw = raw.removePrefix(prefix).trim()
                                break
                            }
                        }
                    }

                    // 1. 如果有 Regex，且有 Group，返回 Group 1
                    if (handler.regex != null && handler.regex.isNotEmpty()) {
                        val match = Regex(handler.regex).find(raw)
                        if (match != null && match.groups.size > 1) {
                            return@map match.groups[1]?.value ?: ""
                        }
                    }
                    // 2. 否则，直接返回内容
                    return@map raw
                }
            }

            null 
        }.toTypedArray()
    }
}
