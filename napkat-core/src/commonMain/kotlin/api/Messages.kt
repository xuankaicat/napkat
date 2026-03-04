package io.github.xuankaicat.napkat.core.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface IMessage {
    val type: String
    val data: Any
}

/**
 * 纯文本消息段
 *
 * @param type 消息类型 (text)
 * @param data 纯文本消息数据
 */
@Serializable
@SerialName("text")
data class TextMessage(
    override val data: TextData
) : IMessage {
    @Transient override val type: String = "text"

    constructor(data: String) : this(TextData(data))
    val result: String get() = data.text

    /**
     * 纯文本消息数据
     *
     * @param text 纯文本内容
     */
    @Serializable
    data class TextData(val text: String)
}

/**
 * QQ表情消息段
 *
 * @param type 消息类型 (face)
 * @param data QQ表情消息数据
 */
@Serializable
@SerialName("face")
data class FaceMessage(
    override val data: FaceData
) : IMessage {
    @Transient override val type: String = "face"

    constructor(data: String) : this(FaceData(data))
    val result: String get() = data.id

    /**
     * QQ表情消息数据
     *
     * @param id 表情ID
     */
    @Serializable
    data class FaceData(val id: String)
}

/**
 * 商城表情消息段
 *
 * @param type 消息类型 (mface)
 * @param data 商城表情消息数据
 */
@Serializable
@SerialName("mface")
data class MFaceMessage(
    override val data: MFaceData
) : IMessage {
    @Transient override val type: String = "mface"

    /**
     * 商城表情消息数据
     *
     * @param emojiPackageId 表情包ID
     * @param emojiId 表情ID
     * @param key 表情key
     * @param summary 表情摘要
     */
    @Serializable
    data class MFaceData(
        @SerialName("emoji_package_id") val emojiPackageId: Int,
        @SerialName("emoji_id") val emojiId: String,
        val key: String,
        val summary: String
    )
}

/**
 * At 消息段
 *
 * @param type 消息类型 (at)
 * @param data @消息数据
 */
@Serializable
@SerialName("at")
data class AtMessage(
    override val data: AtData
) : IMessage {
    @Transient override val type: String = "at"

    /**
     * At 消息数据
     *
     * @param qq QQ号或all
     * @param name 显示名称
     */
    @Serializable
    data class AtData(
        val qq: String,
        val name: String? = null
    )
}

/**
 * 回复消息段
 *
 * @param type 消息类型 (reply)
 * @param data 回复消息数据
 */
@Serializable
@SerialName("reply")
data class ReplyMessage(
    override val data: ReplyData
) : IMessage {
    @Transient override val type: String = "reply"

    constructor(data: String) : this(ReplyData(data))
    val result: String get() = data.id

    /**
     * 回复消息数据
     *
     * @param id 消息ID
     */
    @Serializable
    data class ReplyData(val id: String)
}

/**
 * 基础文件数据实现
 *
 * @param file 文件路径/URL/file:///
 * @param path 文件路径
 * @param url 文件URL
 * @param name 文件名
 * @param thumb 缩略图
 */
@Serializable
data class BaseFileData(
    val file: String,
    val path: String? = null,
    val url: String? = null,
    val name: String? = null,
    val thumb: String? = null
)

/**
 * 图片消息段
 *
 * @param type 消息类型 (image)
 * @param data 图片消息数据
 */
@Serializable
@SerialName("image")
data class ImageMessage(
    override val data: ImageData
) : IMessage {
    @Transient override val type: String = "image"

    /**
     * 图片消息数据
     *
     * @param file 文件路径/URL/file:///
     * @param path 文件路径
     * @param url 文件URL
     * @param name 文件名
     * @param thumb 缩略图
     * @param summary 图片摘要
     * @param subType 图片子类型
     */
    @Serializable
    data class ImageData(
        val file: String,
        val path: String? = null,
        val url: String? = null,
        val name: String? = null,
        val thumb: String? = null,
        val summary: String? = null,
        @SerialName("sub_type") val subType: Int? = null
    )
}

/**
 * 语音消息段
 *
 * @param type 消息类型 (record)
 * @param data 语音消息数据
 */
@Serializable
@SerialName("record")
data class RecordMessage(
    override val data: BaseFileData
) : IMessage {
    @Transient override val type: String = "record"
}

/**
 * 视频消息段
 *
 * @param type 消息类型 (video)
 * @param data 视频消息数据
 */
@Serializable
@SerialName("video")
data class VideoMessage(
    override val data: BaseFileData
) : IMessage {
    @Transient override val type: String = "video"
}

/**
 * 文件消息段
 *
 * @param type 消息类型 (file)
 * @param data 文件消息数据
 */
@Serializable
@SerialName("file")
data class FileMessage(
    override val data: BaseFileData
) : IMessage {
    @Transient override val type: String = "file"
}

/**
 * 音乐消息段
 *
 * @param type 消息类型 (music)
 * @param data 音乐消息数据
 */
@Serializable
@SerialName("music")
data class MusicMessage(
    override val data: IMusicData
) : IMessage {
    @Transient override val type: String = "music"
}

/**
 * 音乐数据接口
 *
 * @property type 音乐平台类型
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("type")
sealed interface IMusicData {
    val type: String
}

/**
 * 自定义音乐数据
 *
 * @param type 音乐平台类型 (custom)
 * @param url 点击后跳转URL
 * @param audio 音频URL
 * @param title 音乐标题
 * @param image 封面图片URL
 * @param content 音乐简介
 */
@Serializable
@SerialName("custom")
data class CustomMusicData(
    val url: String,
    val audio: String,
    val title: String,
    val image: String? = null,
    val content: String? = null
) : IMusicData {
    @Transient override val type: String = "custom"
}

/**
 * QQ音乐数据
 *
 * @param type 音乐平台类型 (qq)
 * @param id 音乐ID
 */
@Serializable
@SerialName("qq")
data class QQMusicData(val id: String) : IMusicData {
    @Transient override val type: String = "qq"
}

/**
 * 网易云音乐数据
 *
 * @param type 音乐平台类型 (163)
 * @param id 音乐ID
 */
@Serializable
@SerialName("163")
data class NeteaseMusicData(val id: String) : IMusicData {
    @Transient override val type: String = "163"
}

/**
 * 酷狗音乐数据
 *
 * @param type 音乐平台类型 (kugou)
 * @param id 音乐ID
 */
@Serializable
@SerialName("kugou")
data class KugouMusicData(val id: String) : IMusicData {
    @Transient override val type: String = "kugou"
}

/**
 * 咪咕音乐数据
 *
 * @param type 音乐平台类型 (migu)
 * @param id 音乐ID
 */
@Serializable
@SerialName("migu")
data class MiguMusicData(val id: String) : IMusicData {
    @Transient override val type: String = "migu"
}

/**
 * 酷我音乐数据
 *
 * @param type 音乐平台类型 (kuwo)
 * @param id 音乐ID
 */
@Serializable
@SerialName("kuwo")
data class KuwoMusicData(val id: String) : IMusicData {
    @Transient override val type: String = "kuwo"
}

/**
 * 戳一戳消息段
 *
 * @param type 消息类型 (poke)
 * @param data 戳一戳消息数据
 */
@Serializable
@SerialName("poke")
data class PokeMessage(
    override val data: PokeData
) : IMessage {
    @Transient override val type: String = "poke"

    /**
     * 戳一戳消息数据
     *
     * @param type 戳一戳类型
     * @param id 戳一戳ID
     */
    @Serializable
    data class PokeData(
        val type: String,
        val id: String
    )
}

/**
 * 骰子消息段
 *
 * @param type 消息类型 (dice)
 * @param data 骰子消息数据
 */
@Serializable
@SerialName("dice")
data class DiceMessage(
    override val data: DiceData
) : IMessage {
    @Transient override val type: String = "dice"

    constructor(data: String) : this(DiceData(data))
    val result: String get() = data.result

    /**
     * 骰子消息数据
     *
     * @param result 骰子结果
     */
    @Serializable
    data class DiceData(val result: String)
}

/**
 * 猜拳消息段
 *
 * @param type 消息类型 (rps)
 * @param data 猜拳消息数据
 */
@Serializable
@SerialName("rps")
data class RPSMessage(
    override val data: RPSData
) : IMessage {
    @Transient override val type: String = "rps"

    constructor(data: String) : this(RPSData(data))
    val result: String get() = data.result

    /**
     * 猜拳消息数据
     *
     * @param result 猜拳结果
     */
    @Serializable
    data class RPSData(val result: String)
}

/**
 * 联系人消息段
 *
 * @param type 消息类型 (contact)
 * @param data 联系人消息数据
 */
@Serializable
@SerialName("contact")
data class ContactMessage(
    override val data: ContactData
) : IMessage {
    @Transient override val type: String = "contact"

    /**
     * 联系人消息数据
     *
     * @param type 联系人类型 (qq/group)
     * @param id 联系人ID
     */
    @Serializable
    data class ContactData(
        val type: String, // qq or group
        val id: String
    )
}

/**
 * 位置消息段
 *
 * @param type 消息类型 (location)
 * @param data 位置消息数据
 */
@Serializable
@SerialName("location")
data class LocationMessage(
    override val data: LocationData
) : IMessage {
    @Transient override val type: String = "location"

    /**
     * 位置消息数据
     *
     * @param lat 纬度
     * @param lon 经度
     * @param title 标题
     * @param content 内容
     */
    @Serializable
    data class LocationData(
        val lat: String,
        val lon: String,
        val title: String? = null,
        val content: String? = null
    )
}

/**
 * JSON消息段
 *
 * @param type 消息类型 (json)
 * @param data JSON消息数据
 */
@Serializable
@SerialName("json")
data class JsonMessage(
    override val data: JsonData
) : IMessage {
    @Transient override val type: String = "json"

    constructor(data: String) : this(JsonData(data))
    val result: String get() = data.data

    /**
     * JSON消息数据
     *
     * @param data JSON数据
     */
    @Serializable
    data class JsonData(val data: String)
}

/**
 * XML消息段
 *
 * @param type 消息类型 (xml)
 * @param data XML消息数据
 */
@Serializable
@SerialName("xml")
data class XmlMessage(
    override val data: XmlData
) : IMessage {
    @Transient override val type: String = "xml"

    constructor(data: String) : this(XmlData(data))
    val result: String get() = data.data

    /**
     * XML消息数据
     *
     * @param data XML数据
     */
    @Serializable
    data class XmlData(val data: String)
}

/**
 * Markdown消息段
 *
 * @param type 消息类型 (markdown)
 * @param data Markdown消息数据
 */
@Serializable
@SerialName("markdown")
data class MarkdownMessage(
    override val data: MarkdownData
) : IMessage {
    @Transient override val type: String = "markdown"

    constructor(data: String) : this(MarkdownData(data))
    val result: String get() = data.content

    /**
     * Markdown消息数据
     *
     * @param content Markdown内容
     */
    @Serializable
    data class MarkdownData(val content: String)
}

/**
 * 小程序消息段
 *
 * @param type 消息类型 (miniapp)
 * @param data 小程序消息数据
 */
@Serializable
@SerialName("miniapp")
data class MiniAppMessage(
    override val data: MiniAppData
) : IMessage {
    @Transient override val type: String = "miniapp"

    constructor(data: String) : this(MiniAppData(data))
    val result: String get() = data.data

    /**
     * 小程序消息数据
     *
     * @param data 小程序数据
     */
    @Serializable
    data class MiniAppData(val data: String)
}

// Node and Forward messages are recursive, involving List<IMessage<*>>.

/**
 * 合并转发消息节点
 *
 * @param type 消息类型 (node)
 * @param data 合并转发消息节点数据
 */
@Serializable
@SerialName("node")
data class NodeMessage(
    override val data: NodeData
) : IMessage {
    @Transient override val type: String = "node"

    /**
     * 合并转发消息节点数据
     *
     * @param id 转发消息ID
     * @param userId 发送者QQ号
     * @param nickname 发送者昵称
     * @param content 消息内容
     */
    @Serializable
    data class NodeData(
        val id: String? = null,
        @SerialName("user_id") val userId: String? = null,
        val nickname: String? = null,
        val content: List<IMessage>? = null
    )
}

/**
 * 合并转发消息段
 *
 * @param type 消息类型 (forward)
 * @param data 合并转发消息数据
 */
@Serializable
@SerialName("forward")
data class ForwardMessage(
    override val data: ForwardData
) : IMessage {
    @Transient override val type: String = "forward"

    /**
     * 合并转发消息数据
     *
     * @param id 合并转发ID
     * @param content 消息内容
     */
    @Serializable
    data class ForwardData(
        val id: String,
        val content: List<IMessage>? = null
    )
}

/**
 * 在线文件消息段
 *
 * @param type 消息类型 (onlinefile)
 * @param data 在线文件消息数据
 */
@Serializable
@SerialName("onlinefile")
data class OnlineFileMessage(
    override val data: OnlineFileData
) : IMessage {
    @Transient override val type: String = "onlinefile"

    /**
     * 在线文件消息数据
     *
     * @param msgId 消息ID
     * @param elementId 元素ID
     * @param fileName 文件名
     * @param fileSize 文件大小
     * @param isDir 是否为目录
     */
    @Serializable
    data class OnlineFileData(
        val msgId: String,
        val elementId: String,
        val fileName: String,
        val fileSize: String,
        val isDir: Boolean
    )
}

/**
 * QQ闪传消息段
 *
 * @param type 消息类型 (flashtransfer)
 * @param data QQ闪传消息数据
 */
@Serializable
@SerialName("flashtransfer")
data class FlashTransferMessage(
    override val data: FlashTransferData
) : IMessage {
    @Transient override val type: String = "flashtransfer"

    constructor(data: String) : this(FlashTransferData(data))
    val result: String get() = data.fileSetId

    /**
     * QQ闪传消息数据
     *
     * @param fileSetId 文件集ID
     */
    @Serializable
    data class FlashTransferData(
        val fileSetId: String
    )
}

/**
 * 发送者信息
 *
 * @param userId 发送者QQ号
 * @param nickname 昵称
 * @param card 群名片/备注
 * @param role 角色 (owner/admin/member)
 */
@Serializable
data class Sender(
    @SerialName("user_id") val userId: Long,
    val nickname: String,
    val card: String? = null,
    val role: String? = null
)

/**
 * 消息数据
 *
 * @param time 发送时间
 * @param messageType 消息类型 (private/group)
 * @param messageId 消息ID
 * @param realId 真实ID
 * @param messageSeq 消息序号
 * @param sender 发送者
 * @param message 消息内容
 * @param rawMessage 原始消息内容
 * @param font 字体
 * @param groupId 群号
 */
@Serializable
data class MessageData(
    val time: Long,
    @SerialName("message_type") val messageType: String,
    @SerialName("message_id") val messageId: Long,
    @SerialName("real_id") val realId: Long,
    @SerialName("message_seq") val messageSeq: Long,
    val sender: Sender,
    val message: List<IMessage>,
    @SerialName("raw_message") val rawMessage: String,
    val font: Int,
    @SerialName("group_id") val groupId: Long? = null
)

/**
 * 合并转发消息数据
 *
 * @param messages 消息列表
 */
@Serializable
data class ForwardMsgData(
    val messages: List<MessageData>
)

/**
 * 好友信息
 *
 * @param userId 用户QQ号
 * @param nickname 昵称
 * @param remark 备注
 */
@Serializable
data class Friend(
    @SerialName("user_id") val userId: Long,
    val nickname: String,
    val remark: String
)

/**
 * 陌生人信息
 *
 * @param userId 用户QQ号
 * @param nickname 昵称
 * @param sex 性别 (male/female/unknown)
 * @param age 年龄
 * @param qid QID
 * @param level 等级
 * @param loginDays 登录天数
 */
@Serializable
data class StrangerInfo(
    @SerialName("user_id") val userId: Long,
    val nickname: String,
    val sex: String,
    val age: Int,
    val qid: String? = null,
    val level: Int,
    @SerialName("login_days") val loginDays: Int
)

/**
 * 登录号信息
 *
 * @param userId 用户QQ号
 * @param nickname 昵称
 */
@Serializable
data class LoginInfo(
    @SerialName("user_id") val userId: Long,
    val nickname: String
)

/**
 * Cookies
 *
 * @param cookies Cookies
 * @param bkn CSRF Token
 */
@Serializable
data class Cookies(
    val cookies: String,
    val bkn: String
)

/**
 * CSRF Token
 *
 * @param token CSRF Token
 */
@Serializable
data class CsrfToken(
    val token: String
)

/**
 * 登录凭证
 *
 * @param cookies Cookies
 * @param csrfToken CSRF Token
 */
@Serializable
data class Credentials(
    val cookies: String,
    @SerialName("csrf_token") val csrfToken: String
)

/**
 * 机器人 UIN 范围
 *
 * @param minUin 最小 UIN
 * @param maxUin 最大 UIN
 */
@Serializable
data class RobotUinRange(
    @SerialName("min_uin") val minUin: Long,
    @SerialName("max_uin") val maxUin: Long
)

/**
 * 好友分类
 *
 * @param categoryId 分类 ID
 * @param categoryName 分类名称
 * @param categoryMbCount 分类成员数量
 * @param buddyList 好友列表
 */
@Serializable
data class FriendCategory(
    val categoryId: Int,
    val categoryName: String,
    val categoryMbCount: Int,
    val buddyList: List<Friend>
)

/**
 * 单向好友
 *
 * @param userId 用户QQ号
 * @param uid UID
 * @param nickname 昵称
 * @param source 来源
 */
@Serializable
data class UnidirectionalFriend(
    @SerialName("user_id") val userId: Long,
    val uid: String,
    @SerialName("nickname") val nickname: String,
    val source: String
)

/**
 * 群信息
 *
 * @param groupId 群号
 * @param groupName 群名称
 * @param memberCount 成员数
 * @param maxMemberCount 最大成员数
 */
@Serializable
data class GroupInfo(
    @SerialName("group_id") val groupId: Long,
    @SerialName("group_name") val groupName: String,
    @SerialName("member_count") val memberCount: Int,
    @SerialName("max_member_count") val maxMemberCount: Int
)

/**
 * 群成员信息
 *
 * @param groupId 群号
 * @param userId 用户QQ号
 * @param nickname 昵称
 * @param card 群名片
 * @param role 角色 (owner/admin/member)
 * @param sex 性别
 * @param age 年龄
 * @param area 地区
 * @param joinTime 入群时间
 * @param lastSentTime 最后发言时间
 * @param levelLevel 等级
 * @param role 角色
 * @param unfriendly 不友好
 * @param title 专属头衔
 * @param titleExpireTime 头衔过期时间
 * @param cardChangeable 是否允许修改群名片
 */
@Serializable
data class GroupMemberInfo(
    @SerialName("group_id") val groupId: Long,
    @SerialName("user_id") val userId: Long,
    val nickname: String,
    val card: String? = null,
    val role: String, // owner, admin, member
    val sex: String? = null,
    val age: Int? = null,
    val area: String? = null,
    @SerialName("join_time") val joinTime: Int? = null,
    @SerialName("last_sent_time") val lastSentTime: Int? = null,
    @SerialName("level") val levelLevel: String? = null,
    val unfriendly: Boolean? = null,
    val title: String? = null,
    @SerialName("title_expire_time") val titleExpireTime: Int? = null,
    @SerialName("card_changeable") val cardChangeable: Boolean? = null
)

/**
 * 群荣誉信息
 *
 * @param groupId 群号
 * @param currentTalkative 当前龙王
 * @param talkativeList 历史龙王
 * @param performerList 群聊之火
 * @param legendList 群聊炽焰
 * @param strongNewbieList 冒尖小春笋
 * @param emotionList 快乐源泉
 */
@Serializable
data class GroupHonorInfo(
    @SerialName("group_id") val groupId: Long,
    @SerialName("current_talkative") val currentTalkative: HonorUser? = null,
    @SerialName("talkative_list") val talkativeList: List<HonorUser>? = null,
    @SerialName("performer_list") val performerList: List<HonorUser>? = null,
    @SerialName("legend_list") val legendList: List<HonorUser>? = null,
    @SerialName("strong_newbie_list") val strongNewbieList: List<HonorUser>? = null,
    @SerialName("emotion_list") val emotionList: List<HonorUser>? = null
)

@Serializable
data class HonorUser(
    @SerialName("user_id") val userId: Long,
    val nickname: String,
    @SerialName("avatar") val avatar: String,
    val description: String? = null
)

/**
 * 版本信息
 *
 * @param appName 应用名称
 * @param appVersion 应用版本
 * @param protocolVersion 协议版本
 */
@Serializable
data class VersionInfo(
    @SerialName("app_name") val appName: String,
    @SerialName("app_version") val appVersion: String,
    @SerialName("protocol_version") val protocolVersion: String
)

/**
 * 运行状态
 *
 * @param online 是否在线
 * @param good 状态是否良好
 */
@Serializable
data class Status(
    val online: Boolean,
    val good: Boolean
)

/**
 * OCR 结果
 *
 * @param texts 文本列表
 * @param language 语言
 */
@Serializable
data class OcrResult(
    val texts: List<OcrText>,
    val language: String
)

@Serializable
data class OcrText(
    val text: String,
    val confidence: Int,
    val coordinates: List<Coordinate>
)

@Serializable
data class Coordinate(
    val x: Int,
    val y: Int
)

/**
 * 在线客户端
 *
 * @param appId App ID
 * @param deviceName 设备名称
 * @param deviceKind 设备类型
 */
@Serializable
data class Device(
    @SerialName("app_id") val appId: Long,
    @SerialName("device_name") val deviceName: String,
    @SerialName("device_kind") val deviceKind: String
)

/**
 * 文件信息
 *
 * @param fileId 文件ID
 * @param fileName 文件名
 * @param fileSize 文件大小
 * @param busid busid
 */
@Serializable
data class FileInfo(
    @SerialName("file_id") val fileId: String,
    @SerialName("file_name") val fileName: String,
    @SerialName("file_size") val fileSize: Long,
    val busid: Int
)

/**
 * 语音文件信息
 *
 * @param file 文件名
 * @param url 下载链接
 */
@Serializable
data class RecordInfo(
    val file: String,
    val url: String
)

/**
 * 图片文件信息
 *
 * @param file 文件名
 * @param url 下载链接
 */
@Serializable
data class ImageInfo(
    val file: String,
    val url: String
)

/**
 * 群文件系统信息
 *
 * @param fileCount 文件数量
 * @param limitCount 限制数量
 * @param usedSpace 已用空间
 * @param totalSpace 总空间
 */
@Serializable
data class GroupFileSystemInfo(
    @SerialName("file_count") val fileCount: Int,
    @SerialName("limit_count") val limitCount: Int,
    @SerialName("used_space") val usedSpace: Long,
    @SerialName("total_space") val totalSpace: Long
)

/**
 * 群文件/文件夹
 *
 * @param groupId 群号
 * @param fileId 文件ID
 * @param fileName 文件名
 * @param busid busid
 * @param fileSize 文件大小
 * @param uploadTime 上传时间
 * @param deadTime 过期时间
 * @param modifyTime 修改时间
 * @param downloadTimes 下载次数
 * @param uploader 上传者
 * @param uploaderName 上传者昵称
 */
@Serializable
data class GroupFile(
    @SerialName("group_id") val groupId: Long,
    @SerialName("file_id") val fileId: String,
    @SerialName("file_name") val fileName: String,
    val busid: Int,
    @SerialName("file_size") val fileSize: Long,
    @SerialName("upload_time") val uploadTime: Long,
    @SerialName("dead_time") val deadTime: Long,
    @SerialName("modify_time") val modifyTime: Long,
    @SerialName("download_times") val downloadTimes: Int,
    @SerialName("uploader") val uploader: Long,
    @SerialName("uploader_name") val uploaderName: String
)

/**
 * 群文件夹
 *
 * @param groupId 群号
 * @param folderId 文件夹ID
 * @param folderName 文件夹名
 * @param createTime 创建时间
 * @param creator 创建者
 * @param creatorName 创建者昵称
 * @param totalFileCount 总文件数
 */
@Serializable
data class GroupFolder(
    @SerialName("group_id") val groupId: Long,
    @SerialName("folder_id") val folderId: String,
    @SerialName("folder_name") val folderName: String,
    @SerialName("create_time") val createTime: Long,
    @SerialName("creator") val creator: Long,
    @SerialName("creator_name") val creatorName: String,
    @SerialName("total_file_count") val totalFileCount: Int
)

/**
 * 群文件列表响应
 *
 * @param files 文件列表
 * @param folders 文件夹列表
 */
@Serializable
data class GroupFilesResponse(
    val files: List<GroupFile>,
    val folders: List<GroupFolder>
)

/**
 * 精华消息
 *
 * @param senderId 发送者ID
 * @param senderNick 发送者昵称
 * @param senderTime 发送时间
 * @param operatorId 操作者ID
 * @param operatorNick 操作者昵称
 * @param operatorTime 操作时间
 * @param messageId 消息ID
 */
@Serializable
data class EssenceMsg(
    @SerialName("sender_id") val senderId: Long,
    @SerialName("sender_nick") val senderNick: String,
    @SerialName("sender_time") val senderTime: Long,
    @SerialName("operator_id") val operatorId: Long,
    @SerialName("operator_nick") val operatorNick: String,
    @SerialName("operator_time") val operatorTime: Long,
    @SerialName("message_id") val messageId: Long
)

@Serializable
data class PacketStatus(
    @SerialName("packet_received") val packetReceived: Long,
    @SerialName("packet_sent") val packetSent: Long,
    @SerialName("packet_lost") val packetLost: Long
)

@Serializable
data class RKeyInfo(
    val type: String,
    val rkey: String,
    @SerialName("created_at") val createdAt: Long,
    val ttl: Long
)

@Serializable
data class RKeyServerInfo(
    @SerialName("private_rkey") val privateRkey: String,
    @SerialName("group_rkey") val groupRkey: String,
    @SerialName("expired_time") val expiredTime: Long,
    val name: String
)

@Serializable
data class NcRKeyInfo(
    val key: String,
    val expired: Long
)

@Serializable
data class ClientKeyInfo(
    val clientkey: String
)

@Serializable
data class EmojiLike(
    @SerialName("user_id") val userId: String,
    @SerialName("nick_name") val nickName: String
)

@Serializable
data class EmojiLikeList(
    @SerialName("emoji_like_list") val emojiLikeList: List<EmojiLike>
)

@Serializable
data class AiRecordResponse(
    @SerialName("message_id") val messageId: Long
)

