package io.github.xuankaicat.napkat.spring.boot.starter.sample

import io.github.xuankaicat.napkat.core.bot.Bot
import io.github.xuankaicat.napkat.spring.boot.starter.annotation.*
import org.springframework.beans.factory.annotation.Autowired

@NapCatController(prefix = ["/", "#"])
class TestController {
    @Autowired
    lateinit var mockBot: Bot

    @OnGroupMessage(regex = "ping")
    suspend fun handlePing(@MsgBody msg: String, @GroupId groupId: Long) {
        mockBot.sendGroupMsg(groupId.toString(), "pong")
    }

    @OnGroupMessage(startsWith = ["echo"])
    suspend fun handleEcho(@Param msg: String, @GroupId groupId: Long) {
        mockBot.sendGroupMsg(groupId.toString(), msg)
    }

    @OnPrivateMessage(startsWith = ["hello"])
    suspend fun handleHello(@SenderId userId: Long) {
        mockBot.sendPrivateMsg(userId.toString(), "world")
    }
}
