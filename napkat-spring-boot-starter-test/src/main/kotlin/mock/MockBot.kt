package io.github.xuankaicat.napkat.spring.boot.starter.test.mock

import io.github.xuankaicat.napkat.core.api.*
import io.github.xuankaicat.napkat.core.bot.WebSocketBot
import io.github.xuankaicat.napkat.core.event.GroupMessageEvent
import io.github.xuankaicat.napkat.core.event.NapCatEvent
import io.github.xuankaicat.napkat.core.event.NapCatEventListener
import io.github.xuankaicat.napkat.core.event.Sender
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import kotlinx.serialization.json.JsonPrimitive

class MockBot : WebSocketBot(
    client = HttpClient(MockEngine { respondOk() }),
    host = "localhost",
    port = 8080
) {
    private val listeners = mutableListOf<NapCatEventListener>()
    val sentMessages = mutableListOf<SentMessage>()

    data class SentMessage(
        val type: String,
        val targetId: String,
        val content: Any
    )

    override fun registerListener(listener: NapCatEventListener) {
        listeners.add(listener)
        // Also register to super class if needed, but since we override connect/send, maybe not?
        // But WebSocketBot handles event dispatching. 
        // We should probably expose a method to simulate incoming event.
        super.registerListener(listener)
    }

    suspend fun simulateEvent(event: NapCatEvent) {
        // We need to access listeners. 
        // Since listeners in WebSocketBot is private, we can't access it directly unless we use reflection or super method if available.
        // But WebSocketBot has a `handleEvent` method which is private.
        // However, `onEvent` is on the listener.
        // We can maintain our own list of listeners or use reflection to get super listeners.
        // Actually, we can just call onEvent on all registered listeners.
        // Since we called super.registerListener, we can't easily access them.
        // But wait, if we override registerListener, we can keep track.
        listeners.forEach { it.onEvent(event) }
    }

    /**
     * 模拟接收群消息
     */
    suspend fun receiveGroupMsg(groupId: String, message: String, userId: String = "123456") {
        val event = GroupMessageEvent(
            time = System.currentTimeMillis() / 1000,
            selfId = 12345,
            postType = "message",
            messageType = "group",
            subType = "normal",
            messageId = System.currentTimeMillis() % 10000,
            userId = userId.toLong(),
            groupId = groupId.toLong(),
            message = JsonPrimitive(message),
            rawMessage = message,
            font = 0,
            sender = Sender(userId = userId.toLong(), nickname = "TestUser")
        )
        simulateEvent(event)
    }

    /**
     * 模拟接收私聊消息
     */
    suspend fun receivePrivateMsg(userId: String, message: String) {
        val event = io.github.xuankaicat.napkat.core.event.PrivateMessageEvent(
            time = System.currentTimeMillis() / 1000,
            selfId = 12345,
            postType = "message",
            messageType = "private",
            subType = "friend",
            messageId = System.currentTimeMillis() % 10000,
            userId = userId.toLong(),
            message = JsonPrimitive(message),
            rawMessage = message,
            font = 0,
            sender = Sender(userId = userId.toLong(), nickname = "TestUser")
        )
        simulateEvent(event)
    }

    // Override API methods to record calls instead of sending WS
    override suspend fun sendGroupMsg(groupId: String, message: String): NapCatResponse<SendMsgResponse> {
        sentMessages.add(SentMessage("group", groupId, message))
        return NapCatResponse(status = "ok", retcode = 0, data = SendMsgResponse(123))
    }

    override suspend fun sendPrivateMsg(userId: String, message: String): NapCatResponse<SendMsgResponse> {
        sentMessages.add(SentMessage("private", userId, message))
        return NapCatResponse(status = "ok", retcode = 0, data = SendMsgResponse(456))
    }
}
