package io.github.xuankaicat.napkat.core.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * NapCat 事件基类
 *
 * 所有 OneBot 11/NapCat 上报的事件都继承自此类。
 *
 * @property time 事件发生的时间戳
 * @property selfId 收到事件的机器人 QQ 号
 * @property postType 上报类型 (message, notice, request, meta_event)
 */
@Serializable
sealed class NapCatEvent {
    abstract val time: Long
    abstract val selfId: Long
    abstract val postType: String
}

/**
 * 发送者信息
 *
 * @property userId 发送者 QQ 号
 * @property nickname 昵称
 * @property sex 性别 (male, female, unknown)
 * @property age 年龄
 * @property card 群名片/备注
 * @property area 地区
 * @property level 等级
 * @property role 角色 (owner, admin, member)
 * @property title 头衔
 */
@Serializable
data class Sender(
    @SerialName("user_id") val userId: Long? = null,
    val nickname: String? = null,
    val sex: String? = null,
    val age: Int? = null,
    val card: String? = null,
    val area: String? = null,
    val level: String? = null,
    val role: String? = null,
    val title: String? = null
)

/**
 * 匿名信息
 *
 * @property id 匿名用户 ID
 * @property name 匿名用户名称
 * @property flag 匿名用户 flag
 */
@Serializable
data class Anonymous(
    val id: Long,
    val name: String,
    val flag: String
)

/**
 * 消息事件基类
 *
 * @property messageType 消息类型 (group, private)
 * @property subType 子类型 (normal, anonymous, notice, friend, group, other)
 * @property messageId 消息 ID
 * @property userId 发送者 QQ 号
 * @property message 消息内容 (可能是 String 或 Array)
 * @property rawMessage 原始消息内容
 * @property font 字体
 * @property sender 发送者信息
 */
@Serializable
@SerialName("message")
sealed class MessageEvent : NapCatEvent() {
    abstract val messageType: String
    abstract val subType: String
    abstract val messageId: Long
    abstract val userId: Long
    abstract val message: JsonElement // Could be String or Array
    abstract val rawMessage: String
    abstract val font: Int
    abstract val sender: Sender
}

/**
 * 群消息事件
 *
 * @property groupId 群号
 * @property anonymous 匿名信息 (如果存在)
 */
@Serializable
@SerialName("group")
data class GroupMessageEvent(
    override val time: Long,
    @SerialName("self_id") override val selfId: Long,
    @SerialName("post_type") override val postType: String = "message",
    @SerialName("message_type") override val messageType: String = "group",
    @SerialName("sub_type") override val subType: String,
    @SerialName("message_id") override val messageId: Long,
    @SerialName("user_id") override val userId: Long,
    @SerialName("group_id") val groupId: Long,
    override val message: JsonElement,
    @SerialName("raw_message") override val rawMessage: String,
    override val font: Int,
    override val sender: Sender,
    val anonymous: Anonymous? = null
) : MessageEvent()

/**
 * 私聊消息事件
 *
 * @property targetId 接收者 QQ 号 (通常是 selfId)
 * @property tempSource 临时会话来源
 */
@Serializable
@SerialName("private")
data class PrivateMessageEvent(
    override val time: Long,
    @SerialName("self_id") override val selfId: Long,
    @SerialName("post_type") override val postType: String = "message",
    @SerialName("message_type") override val messageType: String = "private",
    @SerialName("sub_type") override val subType: String,
    @SerialName("message_id") override val messageId: Long,
    @SerialName("user_id") override val userId: Long,
    @SerialName("target_id") val targetId: Long? = null, // 有时候是 selfId
    override val message: JsonElement,
    @SerialName("raw_message") override val rawMessage: String,
    override val font: Int,
    override val sender: Sender,
    @SerialName("temp_source") val tempSource: Int? = null
) : MessageEvent()

/**
 * 通知事件
 *
 * @property noticeType 通知类型 (group_upload, group_admin, group_decrease, group_increase, group_ban, friend_add, notify, etc.)
 * @property subType 子类型 (poke, lucky_king, honor, etc.)
 * @property userId 操作者 QQ 号 (可能为空)
 * @property groupId 群号 (可能为空)
 * @property operatorId 操作者 QQ 号 (与 userId 类似，视具体事件而定)
 * @property messageId 关联的消息 ID (如撤回消息事件)
 * @property file 文件信息 (如群文件上传事件)
 */
@Serializable
@SerialName("notice")
data class NoticeEvent(
    override val time: Long,
    @SerialName("self_id") override val selfId: Long,
    @SerialName("post_type") override val postType: String = "notice",
    @SerialName("notice_type") val noticeType: String,
    @SerialName("sub_type") val subType: String? = null,
    @SerialName("user_id") val userId: Long? = null,
    @SerialName("group_id") val groupId: Long? = null,
    @SerialName("operator_id") val operatorId: Long? = null,
    @SerialName("message_id") val messageId: Long? = null,
    val file: JsonElement? = null // 文件相关通知
) : NapCatEvent()

/**
 * 请求事件
 *
 * @property requestType 请求类型 (friend, group)
 * @property subType 子类型 (add, invite)
 * @property userId 发送请求的 QQ 号
 * @property groupId 群号 (仅群请求有效)
 * @property comment 验证信息
 * @property flag 请求 flag (用于处理请求)
 */
@Serializable
@SerialName("request")
data class RequestEvent(
    override val time: Long,
    @SerialName("self_id") override val selfId: Long,
    @SerialName("post_type") override val postType: String = "request",
    @SerialName("request_type") val requestType: String,
    @SerialName("sub_type") val subType: String? = null, // add/invite
    @SerialName("user_id") val userId: Long,
    @SerialName("group_id") val groupId: Long? = null,
    val comment: String? = null,
    val flag: String
) : NapCatEvent()

/**
 * 元事件
 *
 * @property metaEventType 元事件类型 (lifecycle, heartbeat)
 * @property status 状态信息
 * @property interval 心跳间隔
 */
@Serializable
@SerialName("meta_event")
data class MetaEvent(
    override val time: Long,
    @SerialName("self_id") override val selfId: Long,
    @SerialName("post_type") override val postType: String = "meta_event",
    @SerialName("meta_event_type") val metaEventType: String,
    val status: JsonElement? = null,
    val interval: Long? = null
) : NapCatEvent()
