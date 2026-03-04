package io.github.xuankaicat.napkat.spring.boot.starter.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("napcat")
data class NapcatProperties(
    val websocketHost: String = "localhost",
    val websocketPort: Int = 8080,
    val token: String? = null
)
