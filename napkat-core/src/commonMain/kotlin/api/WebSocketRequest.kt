package io.github.xuankaicat.napkat.core.api

import kotlinx.serialization.Serializable

@Serializable
data class WebSocketRequest(
    val action: String,
    val params: Map<String, String> = emptyMap(),
    val echo: String = "",
)
