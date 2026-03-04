package io.github.xuankaicat.napkat.spring.boot.starter.annotation

import io.github.xuankaicat.napkat.core.event.NapCatEvent
import kotlin.reflect.KClass

/**
 * 事件映射注解
 *
 * 用于标注在事件处理注解上，指定该注解对应的事件处理器。
 * Dispatcher 会根据此元注解找到对应的 Processor 来处理事件匹配逻辑。
 *
 * @property processor 事件处理器类，必须实现 [EventProcessor] 接口
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EventMapper(val processor: KClass<out EventProcessor<*>>)

/**
 * 事件处理器接口
 *
 * 用于解析事件注解并生成事件匹配逻辑。
 * 每个事件注解（如 @OnGroupMessage）都需要对应一个 Processor 实现。
 *
 * @param A 对应的注解类型
 */
interface EventProcessor<A : Annotation> {
    /**
     * 获取该处理器关注的事件类型
     *
     * @return 事件类 Class 对象
     */
    fun getEventType(): Class<out NapCatEvent>

    /**
     * 解析注解并生成匹配条件
     *
     * @param annotation 注解实例
     * @return 一个函数，接受事件对象，返回是否匹配
     */
    fun resolve(annotation: A): (NapCatEvent) -> Boolean
}
