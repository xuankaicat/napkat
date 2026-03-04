package io.github.xuankaicat.napkat.core.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param status 状态 (ok/failed)
 * @param retcode 返回码
 * @param data 业务数据
 * @param message 消息
 * @param wording 提示
 * @param stream 是否为流式响应 (stream-action/normal-action)
 */
@Serializable
data class NapCatResponse<T>(
    val status: String,
    val retcode: Int,
    val data: T? = null,
    val message: String? = null,
    val wording: String? = null,
    val stream: String? = null
)

/**
 * 发送消息响应
 *
 * @param messageId 消息ID
 */
@Serializable
data class SendMsgResponse(
    @SerialName("message_id") val messageId: Long
)