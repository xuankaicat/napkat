package io.github.xuankaicat.napkat.spring.boot.starter.annotation

import io.github.xuankaicat.napkat.core.event.*

/**
 * 消息内容参数注解
 *
 * 用于将消息事件的原始内容 (rawMessage) 注入到处理函数的参数中。
 * 适用于 [MessageEvent] 及其子类。
 *
 * 示例：
 * ```kotlin
 * fun handle(@MsgBody content: String)
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MsgBody

/**
 * 消息参数注解
 *
 * 用于提取消息内容中除去指令部分后的参数。
 * 当消息以指定前缀开头时，提取前缀之后的内容。
 * 如果使用了 regex，则尝试提取第一个捕获组。
 * 
 * 示例：
 * 消息: "/bot 123456"
 * 注解: @OnGroupMessage(startsWith = ["/bot"])
 * ```kotlin
 * fun handle(@Param arg: String) // arg = "123456" (去除前缀并trim)
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Param

/**
 * 发送者 ID 参数注解
 *
 * 用于将事件的发送者 ID (userId) 注入到处理函数的参数中。
 * 适用于 [MessageEvent], [RequestEvent], [NoticeEvent]。
 *
 * 示例：
 * ```kotlin
 * fun handle(@SenderId userId: Long)
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class SenderId

/**
 * 群号参数注解
 *
 * 用于将事件的群号 (groupId) 注入到处理函数的参数中。
 * 适用于 [GroupMessageEvent], [NoticeEvent], [RequestEvent] 等包含群信息的事件。
 *
 * 示例：
 * ```kotlin
 * fun handle(@GroupId groupId: Long)
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class GroupId

/**
 * 事件对象参数注解
 *
 * 显式标记一个参数用于接收完整的事件对象。
 * 通常情况下，如果参数类型匹配事件类型，会自动注入，此注解可用于消除歧义或增强可读性。
 *
 * 示例：
 * ```kotlin
 * @OnGroupMessage
 * fun handle(@EventBody event: GroupMessageEvent)
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class EventBody

/**
 * 正则匹配结果参数注解
 *
 * 当事件注解 (如 @OnGroupMessage) 配置了 regex 参数时，
 * 可使用此注解将正则匹配结果 (MatchResult) 注入到参数中。
 *
 * 示例：
 * ```kotlin
 * @OnGroupMessage(regex = "^/echo (.*)")
 * fun handle(@MatchResult match: MatchResult)
 * ```
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MatchResult
