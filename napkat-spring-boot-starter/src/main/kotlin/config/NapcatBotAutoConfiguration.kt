package io.github.xuankaicat.napkat.spring.boot.starter.config

import io.github.xuankaicat.napkat.core.bot.WebSocketBot
import io.github.xuankaicat.napkat.spring.boot.starter.dispatcher.NapCatEventDispatcher
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.SmartLifecycle

@AutoConfiguration
@EnableConfigurationProperties(NapcatProperties::class)
@Import(NapCatEventDispatcher::class)
class NapcatBotAutoConfiguration(
    private val napcatProperties: NapcatProperties,
) {

    @Bean
    @ConditionalOnMissingBean
    fun httpClient(): HttpClient {
        return HttpClient {
            install(WebSockets)
        }
    }

    @Bean
    @ConditionalOnMissingBean
    fun webSocketBot(httpClient: HttpClient): WebSocketBot {
        return WebSocketBot(
            client = httpClient,
            host = napcatProperties.websocketHost,
            port = napcatProperties.websocketPort,
            token = napcatProperties.token
        )
    }

    @Bean
    fun botLifecycle(bot: WebSocketBot): SmartLifecycle {
        return object : SmartLifecycle {
            private var running = false
            private val scope = CoroutineScope(Dispatchers.IO)

            override fun start() {
                if (!running) {
                    scope.launch {
                        try {
                            bot.connect()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    running = true
                }
            }

            override fun stop() {
                running = false
            }

            override fun isRunning(): Boolean = running
        }
    }
}
