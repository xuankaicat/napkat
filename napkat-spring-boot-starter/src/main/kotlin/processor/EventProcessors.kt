package io.github.xuankaicat.napkat.spring.boot.starter.processor

import io.github.xuankaicat.napkat.core.event.*
import io.github.xuankaicat.napkat.spring.boot.starter.annotation.*
import kotlinx.serialization.json.*

// OnEvent
/**
 * 处理 @OnEvent 注解
 * 匹配所有类型的事件。
 */
class OnEventProcessor : EventProcessor<OnEvent> {
    override fun getEventType() = NapCatEvent::class.java
    override fun resolve(annotation: OnEvent): (NapCatEvent) -> Boolean = { true }
}

// OnMessage
/**
 * 处理 @OnMessage 注解
 * 匹配所有消息事件。
 */
class OnMessageProcessor : EventProcessor<OnMessage> {
    override fun getEventType() = MessageEvent::class.java
    override fun resolve(annotation: OnMessage): (NapCatEvent) -> Boolean = { e ->
        if (e is MessageEvent) {
            val regexCheck = annotation.regex.isEmpty() || Regex(annotation.regex).containsMatchIn(e.rawMessage)
            val startCheck = annotation.startsWith.isEmpty() || annotation.startsWith.any { e.rawMessage.startsWith(it) }
            val endCheck = annotation.endsWith.isEmpty() || annotation.endsWith.any { e.rawMessage.endsWith(it) }
            val containsCheck = annotation.contains.isEmpty() || annotation.contains.any { e.rawMessage.contains(it) }
            regexCheck && startCheck && endCheck && containsCheck
        } else false
    }
}

// OnGroupMessage
/**
 * 处理 @OnGroupMessage 注解
 * 匹配群消息，支持群号过滤和正则匹配。
 */
class OnGroupMessageProcessor : EventProcessor<OnGroupMessage> {
    override fun getEventType() = GroupMessageEvent::class.java
    override fun resolve(annotation: OnGroupMessage): (NapCatEvent) -> Boolean = { e ->
        if (e !is GroupMessageEvent) false
        else {
            val regexCheck = annotation.regex.isEmpty() || Regex(annotation.regex).containsMatchIn(e.rawMessage)
            val startCheck = annotation.startsWith.isEmpty() || annotation.startsWith.any { e.rawMessage.startsWith(it) }
            val endCheck = annotation.endsWith.isEmpty() || annotation.endsWith.any { e.rawMessage.endsWith(it) }
            val containsCheck = annotation.contains.isEmpty() || annotation.contains.any { e.rawMessage.contains(it) }
            regexCheck && startCheck && endCheck && containsCheck
        }
    }
}

// OnPrivateMessage
/**
 * 处理 @OnPrivateMessage 注解
 * 匹配私聊消息，支持用户ID过滤和正则匹配。
 */
class OnPrivateMessageProcessor : EventProcessor<OnPrivateMessage> {
    override fun getEventType() = PrivateMessageEvent::class.java
    override fun resolve(annotation: OnPrivateMessage): (NapCatEvent) -> Boolean = { e ->
        if (e !is PrivateMessageEvent) false
        else {
            val regexCheck = annotation.regex.isEmpty() || Regex(annotation.regex).containsMatchIn(e.rawMessage)
            val startCheck = annotation.startsWith.isEmpty() || annotation.startsWith.any { e.rawMessage.startsWith(it) }
            val endCheck = annotation.endsWith.isEmpty() || annotation.endsWith.any { e.rawMessage.endsWith(it) }
            val containsCheck = annotation.contains.isEmpty() || annotation.contains.any { e.rawMessage.contains(it) }
            regexCheck && startCheck && endCheck && containsCheck
        }
    }
}

// OnNotice
/**
 * 处理 @OnNotice 注解
 * 匹配所有通知事件。
 */
class OnNoticeProcessor : EventProcessor<OnNotice> {
    override fun getEventType() = NoticeEvent::class.java
    override fun resolve(annotation: OnNotice): (NapCatEvent) -> Boolean = { it is NoticeEvent }
}

// OnRequest
/**
 * 处理 @OnRequest 注解
 * 匹配所有请求事件。
 */
class OnRequestProcessor : EventProcessor<OnRequest> {
    override fun getEventType() = RequestEvent::class.java
    override fun resolve(annotation: OnRequest): (NapCatEvent) -> Boolean = { it is RequestEvent }
}

// OnMetaEvent
/**
 * 处理 @OnMetaEvent 注解
 * 匹配所有元事件。
 */
class OnMetaEventProcessor : EventProcessor<OnMetaEvent> {
    override fun getEventType() = MetaEvent::class.java
    override fun resolve(annotation: OnMetaEvent): (NapCatEvent) -> Boolean = { it is MetaEvent }
}

// OnGroupPoke
/**
 * 处理 @OnGroupPoke 注解
 * 匹配群内的戳一戳通知。
 */
class OnGroupPokeProcessor : EventProcessor<OnGroupPoke> {
    override fun getEventType() = NoticeEvent::class.java
    override fun resolve(annotation: OnGroupPoke): (NapCatEvent) -> Boolean = { e ->
        e is NoticeEvent && e.subType == "poke" && e.groupId != null
    }
}

// OnGroupAt
/**
 * 处理 @OnGroupAt 注解
 * 匹配群内 @Bot 的消息。
 * 检查消息段中是否包含 type=at 且 qq=selfId。
 */
class OnGroupAtProcessor : EventProcessor<OnGroupAt> {
    override fun getEventType() = GroupMessageEvent::class.java
    override fun resolve(annotation: OnGroupAt): (NapCatEvent) -> Boolean = { e ->
        if (e is GroupMessageEvent) {
            checkGroupAt(e)
        } else false
    }

    private fun checkGroupAt(e: GroupMessageEvent): Boolean {
        // Check if message contains At segment with selfId
        val msg = e.message
        if (msg is JsonArray) {
            for (segment in msg) {
                if (segment is JsonObject) {
                    val type = segment["type"]?.jsonPrimitive?.contentOrNull
                    if (type == "at") {
                        val data = segment["data"]?.jsonObject
                        val qq = data?.get("qq")?.jsonPrimitive?.contentOrNull
                        if (qq == e.selfId.toString()) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }
}

// OnGroupIncrease
/**
 * 处理 @OnGroupIncrease 注解
 * 匹配群成员增加通知。
 */
class OnGroupIncreaseProcessor : EventProcessor<OnGroupIncrease> {
    override fun getEventType() = NoticeEvent::class.java
    override fun resolve(annotation: OnGroupIncrease): (NapCatEvent) -> Boolean = { e ->
        e is NoticeEvent && e.noticeType == "group_increase"
    }
}

// OnGroupDecrease
/**
 * 处理 @OnGroupDecrease 注解
 * 匹配群成员减少通知。
 */
class OnGroupDecreaseProcessor : EventProcessor<OnGroupDecrease> {
    override fun getEventType() = NoticeEvent::class.java
    override fun resolve(annotation: OnGroupDecrease): (NapCatEvent) -> Boolean = { e ->
        e is NoticeEvent && e.noticeType == "group_decrease"
    }
}

// OnGroupRequest
/**
 * 处理 @OnGroupRequest 注解
 * 匹配加群请求。
 */
class OnGroupRequestProcessor : EventProcessor<OnGroupRequest> {
    override fun getEventType() = RequestEvent::class.java
    override fun resolve(annotation: OnGroupRequest): (NapCatEvent) -> Boolean = { e ->
        e is RequestEvent && e.requestType == "group"
    }
}

// OnStartup
/**
 * 处理 @OnStartup 注解
 * 匹配生命周期连接成功事件 (lifecycle connect)。
 */
class OnStartupProcessor : EventProcessor<OnStartup> {
    override fun getEventType() = MetaEvent::class.java
    override fun resolve(annotation: OnStartup): (NapCatEvent) -> Boolean = { e ->
        if (e is MetaEvent) {
            e.metaEventType == "lifecycle"
        } else false
    }
}

// OnHeartbeat
/**
 * 处理 @OnHeartbeat 注解
 * 匹配心跳事件。
 */
class OnHeartbeatProcessor : EventProcessor<OnHeartbeat> {
    override fun getEventType() = MetaEvent::class.java
    override fun resolve(annotation: OnHeartbeat): (NapCatEvent) -> Boolean = { e ->
        e is MetaEvent && e.metaEventType == "heartbeat"
    }
}
