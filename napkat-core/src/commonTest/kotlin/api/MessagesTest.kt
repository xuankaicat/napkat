package io.github.xuankaicat.napkat.core.api

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MessagesTest {
    private val json = Json { 
        ignoreUnknownKeys = true 
        // classDiscriminator = "type" // Default is "type"
    }

    @Test
    fun testDiceMessageSerialization() {
        val dice = DiceMessage(data = "result")
        // We must serialize as IMessage to get the discriminator
        val serialized = json.encodeToString<IMessage>(dice)

        println("Serialized Dice: $serialized")
        assertTrue(serialized.contains(""""type":"dice""""))
        assertTrue(serialized.contains(""""data":{"result":"result"}"""))

        val deserialized = json.decodeFromString<IMessage>(serialized)
        assertTrue(deserialized is DiceMessage)
        assertEquals("dice", deserialized.type)
        assertEquals("result", deserialized.result)
    }

    @Test
    fun testMixedListSerialization() {
        val list: List<IMessage> = listOf(
            DiceMessage("6"),
            FileMessage(BaseFileData("file.txt"))
        )

        val serialized = json.encodeToString(list)
        println("Serialized List: $serialized")

        val deserialized = json.decodeFromString<List<IMessage>>(serialized)
        assertEquals(2, deserialized.size)
        assertTrue(deserialized[0] is DiceMessage)
        assertTrue(deserialized[1] is FileMessage)
    }
}
