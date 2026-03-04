package io.github.xuankaicat.napkat.spring.boot.starter.sample

import io.github.xuankaicat.napkat.core.event.GroupMessageEvent
import io.github.xuankaicat.napkat.core.event.Sender
import io.github.xuankaicat.napkat.spring.boot.starter.TestConfig
import io.github.xuankaicat.napkat.spring.boot.starter.dispatcher.NapCatEventDispatcher
import io.github.xuankaicat.napkat.spring.boot.starter.test.mock.MockBot
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonPrimitive
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest(classes = [TestConfig::class, NapCatEventDispatcher::class])
@Import(TestConfig::class)
class NapCatControllerTest {
    @Autowired
    lateinit var mockBot: MockBot

    @Autowired
    lateinit var dispatcher: NapCatEventDispatcher

    @Test
    fun testPing() = runBlocking {
        // Construct a GroupMessageEvent
        val event = GroupMessageEvent(
            time = 1234567890,
            selfId = 12345,
            postType = "message",
            messageType = "group",
            subType = "normal",
            messageId = 1,
            userId = 67890,
            groupId = 1001,
            message = JsonPrimitive("/ping"),
            rawMessage = "/ping",
            font = 0,
            sender = Sender(userId = 67890, nickname = "TestUser")
        )

        // Simulate event
        mockBot.simulateEvent(event)
        
        // Wait for coroutine to finish
        // Since dispatcher uses CoroutineScope(Dispatchers.IO), we need to wait a bit or use TestDispatcher.
        // For simplicity, we can use a small delay here, or better, make dispatcher use a test scope.
        // But dispatcher is a bean.
        delay(10)

        // Check if message was sent
        assertEquals(1, mockBot.sentMessages.size)
        val sent = mockBot.sentMessages[0]
        assertEquals("group", sent.type)
        assertEquals("1001", sent.targetId)
        assertEquals("pong", sent.content)
        
        mockBot.sentMessages.clear()
    }

    @Test
    fun testPing2() = runBlocking {
        // 使用 receiveGroupMsg 模拟接收群消息
        mockBot.receiveGroupMsg("1001", "/ping")

        delay(100)

        // Check if message was sent
        assertEquals(1, mockBot.sentMessages.size)
        val sent = mockBot.sentMessages[0]
        assertEquals("group", sent.type)
        assertEquals("1001", sent.targetId)
        assertEquals("pong", sent.content)

        mockBot.sentMessages.clear()
    }

    @Test
    fun testEcho() = runBlocking {
        // Construct a GroupMessageEvent with prefix
        val event = GroupMessageEvent(
            time = 1234567890,
            selfId = 12345,
            postType = "message",
            messageType = "group",
            subType = "normal",
            messageId = 2,
            userId = 67890,
            groupId = 1002,
            message = JsonPrimitive("#echo hello world"),
            rawMessage = "#echo hello world",
            font = 0,
            sender = Sender(userId = 67890, nickname = "TestUser")
        )

        mockBot.simulateEvent(event)
        kotlinx.coroutines.delay(100)

        assertEquals(1, mockBot.sentMessages.size)
        val sent = mockBot.sentMessages[0]
        assertEquals("group", sent.type)
        assertEquals("1002", sent.targetId)
        assertEquals("hello world", sent.content)
        
        mockBot.sentMessages.clear()
    }
    
    @Test
    fun testPrefixMismatch() = runBlocking {
        // No prefix
        val event = GroupMessageEvent(
            time = 1234567890,
            selfId = 12345,
            postType = "message",
            messageType = "group",
            subType = "normal",
            messageId = 3,
            userId = 67890,
            groupId = 1003,
            message = JsonPrimitive("ping"),
            rawMessage = "ping",
            font = 0,
            sender = Sender(userId = 67890, nickname = "TestUser")
        )

        mockBot.simulateEvent(event)
        kotlinx.coroutines.delay(100)

        // Should not trigger because controller requires / or #
        assertEquals(0, mockBot.sentMessages.size)
    }
}
