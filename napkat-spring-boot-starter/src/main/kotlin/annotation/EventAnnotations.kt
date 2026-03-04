package io.github.xuankaicat.napkat.spring.boot.starter.annotation

import io.github.xuankaicat.napkat.spring.boot.starter.processor.*
import io.github.xuankaicat.napkat.core.event.*

/**
 * 通用事件注解
 *
 * 监听所有类型的 [NapCatEvent]。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnEventProcessor::class)
annotation class OnEvent

/**
 * 消息事件注解
 *
 * 监听所有类型的消息事件 ([MessageEvent])，包括群消息和私聊消息。
 *
 * @property regex 正则表达式，用于匹配消息内容。如果非空，只有匹配成功的消息才会触发。
 * @property startsWith 匹配消息前缀。如果非空，只有消息以其中任意一个开头时才会触发。
 * @property endsWith 匹配消息后缀。如果非空，只有消息以其中任意一个结尾时才会触发。
 * @property contains 匹配消息包含的内容。如果非空，只有消息包含其中任意一个时才会触发。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnMessageProcessor::class)
annotation class OnMessage(
    val regex: String = "",
    val startsWith: Array<String> = [],
    val endsWith: Array<String> = [],
    val contains: Array<String> = []
)

/**
 * 群消息事件注解
 *
 * 监听群消息 ([GroupMessageEvent])。
 *
 * @property regex 正则表达式，用于匹配消息内容。如果非空，只有匹配成功的消息才会触发。
 * @property startsWith 匹配消息前缀。如果非空，只有消息以其中任意一个开头时才会触发。
 * @property endsWith 匹配消息后缀。如果非空，只有消息以其中任意一个结尾时才会触发。
 * @property contains 匹配消息包含的内容。如果非空，只有消息包含其中任意一个时才会触发。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnGroupMessageProcessor::class)
annotation class OnGroupMessage(
    val regex: String = "",
    val startsWith: Array<String> = [],
    val endsWith: Array<String> = [],
    val contains: Array<String> = []
)

/**
 * 私聊消息事件注解
 *
 * 监听私聊消息 ([PrivateMessageEvent])。
 *
 * @property regex 正则表达式，用于匹配消息内容。如果非空，只有匹配成功的消息才会触发。
 * @property startsWith 匹配消息前缀。如果非空，只有消息以其中任意一个开头时才会触发。
 * @property endsWith 匹配消息后缀。如果非空，只有消息以其中任意一个结尾时才会触发。
 * @property contains 匹配消息包含的内容。如果非空，只有消息包含其中任意一个时才会触发。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnPrivateMessageProcessor::class)
annotation class OnPrivateMessage(
    val regex: String = "",
    val startsWith: Array<String> = [],
    val endsWith: Array<String> = [],
    val contains: Array<String> = []
)

/**
 * 通知事件注解
 *
 * 监听所有类型的通知事件 ([NoticeEvent])。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnNoticeProcessor::class)
annotation class OnNotice

/**
 * 请求事件注解
 *
 * 监听所有类型的请求事件 ([RequestEvent])，如加群请求、加好友请求。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnRequestProcessor::class)
annotation class OnRequest

/**
 * 元事件注解
 *
 * 监听所有类型的元事件 ([MetaEvent])，如心跳、生命周期事件。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnMetaEventProcessor::class)
annotation class OnMetaEvent

// New Annotations

/**
 * 群戳一戳事件注解
 *
 * 监听群内戳一戳通知。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnGroupPokeProcessor::class)
annotation class OnGroupPoke

/**
 * 群艾特事件注解
 *
 * 监听群内 @机器人的消息。
 * 注意：此事件本质上是 [GroupMessageEvent]，但经过过滤只包含 @当前Bot 的消息。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnGroupAtProcessor::class)
annotation class OnGroupAt

/**
 * 群成员增加事件注解
 *
 * 监听群成员增加通知 (group_increase)。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnGroupIncreaseProcessor::class)
annotation class OnGroupIncrease

/**
 * 群成员减少事件注解
 *
 * 监听群成员减少通知 (group_decrease)。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnGroupDecreaseProcessor::class)
annotation class OnGroupDecrease

/**
 * 加群请求事件注解
 *
 * 监听加群请求 (request_type = group)。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnGroupRequestProcessor::class)
annotation class OnGroupRequest

/**
 * 启动事件注解
 *
 * 监听 Bot 连接成功/启动事件 (lifecycle connect)。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnStartupProcessor::class)
annotation class OnStartup

/**
 * 心跳事件注解
 *
 * 监听心跳包 (meta_event_type = heartbeat)。
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@EventMapper(OnHeartbeatProcessor::class)
annotation class OnHeartbeat
