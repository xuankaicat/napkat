package io.github.xuankaicat.napkat.core.api

/**
 * 消息接口
 *
 * 发送、删除、获取消息相关接口
 */
interface INapCatMessage {
    /**
     * 发送群聊消息 (消息段列表)
     * @param groupId 群号
     * @param message 消息内容 (消息段列表)
     * @see <a href="https://napcat.apifox.cn/77363184f0">https://napcat.apifox.cn/77363184f0</a>
     */
    suspend fun sendGroupMsg(groupId: String, message: List<IMessage>): NapCatResponse<SendMsgResponse>

    /**
     * 发送群聊消息 (单个消息段)
     * @param groupId 群号
     * @param message 消息内容 (单个消息段)
     * @see <a href="https://napcat.apifox.cn/77363184f0">https://napcat.apifox.cn/77363184f0</a>
     */
    suspend fun sendGroupMsg(groupId: String, message: IMessage): NapCatResponse<SendMsgResponse>

    /**
     * 发送群聊消息 (纯文本/CQ码)
     * @param groupId 群号
     * @param message 消息内容 (文本或CQ码)
     * @see <a href="https://napcat.apifox.cn/77363184f0">https://napcat.apifox.cn/77363184f0</a>
     */
    suspend fun sendGroupMsg(groupId: String, message: String): NapCatResponse<SendMsgResponse>

    /**
     * 发送私聊消息 (消息段列表)
     * @param userId 对方QQ号
     * @param message 消息内容 (消息段列表)
     * @see <a href="https://napcat.apifox.cn/77363185f0">https://napcat.apifox.cn/77363185f0</a>
     */
    suspend fun sendPrivateMsg(userId: String, message: List<IMessage>): NapCatResponse<SendMsgResponse>

    /**
     * 发送私聊消息 (单个消息段)
     * @param userId 对方QQ号
     * @param message 消息内容 (单个消息段)
     * @see <a href="https://napcat.apifox.cn/77363185f0">https://napcat.apifox.cn/77363185f0</a>
     */
    suspend fun sendPrivateMsg(userId: String, message: IMessage): NapCatResponse<SendMsgResponse>

    /**
     * 发送私聊消息 (纯文本/CQ码)
     * @param userId 对方QQ号
     * @param message 消息内容 (文本或CQ码)
     * @see <a href="https://napcat.apifox.cn/77363185f0">https://napcat.apifox.cn/77363185f0</a>
     */
    suspend fun sendPrivateMsg(userId: String, message: String): NapCatResponse<SendMsgResponse>

    /**
     * 发送戳一戳
     * @param userId 对方QQ号
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631095e0">https://napcat.apifox.cn/411631095e0</a>
     */
    suspend fun sendPoke(userId: String, groupId: String? = null): NapCatResponse<Unit>

    /**
     * 撤回消息
     * @param messageId 消息ID
     * @see <a href="https://napcat.apifox.cn/411631096e0">https://napcat.apifox.cn/411631096e0</a>
     */
    suspend fun deleteMsg(messageId: Long): NapCatResponse<Unit>

    /**
     * 获取群历史消息
     * @param groupId 群号
     * @param messageSeq 起始消息序号
     * @param count 数量
     * @see <a href="https://napcat.apifox.cn/411631097e0">https://napcat.apifox.cn/411631097e0</a>
     */
    suspend fun getGroupMsgHistory(groupId: Long, messageSeq: Int? = null, count: Int? = null): NapCatResponse<List<MessageData>>

    /**
     * 获取消息详情
     * @param messageId 消息ID
     * @see <a href="https://napcat.apifox.cn/411631098e0">https://napcat.apifox.cn/411631098e0</a>
     */
    suspend fun getMsg(messageId: Long): NapCatResponse<MessageData>

    /**
     * 获取合并转发消息
     * @param messageId 消息ID
     * @see <a href="https://napcat.apifox.cn/411631099e0">https://napcat.apifox.cn/411631099e0</a>
     */
    suspend fun getForwardMsg(messageId: String): NapCatResponse<ForwardMsgData>

    /**
     * 贴表情
     * @param messageId 消息ID
     * @param emojiId 表情ID
     * @see <a href="https://napcat.apifox.cn/411631100e0">https://napcat.apifox.cn/411631100e0</a>
     */
    suspend fun setMsgEmojiLike(messageId: Long, emojiId: String): NapCatResponse<Unit>

    /**
     * 获取好友历史消息
     * @param userId QQ号
     * @param messageSeq 起始消息序号
     * @param count 数量
     * @see <a href="https://napcat.apifox.cn/411631101e0">https://napcat.apifox.cn/411631101e0</a>
     */
    suspend fun getFriendMsgHistory(userId: Long, messageSeq: Int? = null, count: Int? = null): NapCatResponse<List<MessageData>>

    /**
     * 获取贴表情详情
     * @param messageId 消息ID
     * @param emojiId 表情ID
     * @see <a href="https://napcat.apifox.cn/411631102e0">https://napcat.apifox.cn/411631102e0</a>
     */
    suspend fun getEmojiLikes(messageId: String, emojiId: String): NapCatResponse<EmojiLikeList>

    /**
     * 发送合并转发消息
     * @param messages 消息列表
     * @see <a href="https://napcat.apifox.cn/411631103e0">https://napcat.apifox.cn/411631103e0</a>
     */
    suspend fun sendForwardMsg(messages: List<Any>): NapCatResponse<SendMsgResponse>

    /**
     * 获取语音消息详情
     * @param file 语音文件名
     * @param outFormat 输出格式
     * @see <a href="https://napcat.apifox.cn/411631104e0">https://napcat.apifox.cn/411631104e0</a>
     */
    suspend fun getRecord(file: String, outFormat: String): NapCatResponse<RecordInfo>

    /**
     * 获取图片消息详情
     * @param file 图片文件名
     * @see <a href="https://napcat.apifox.cn/411631105e0">https://napcat.apifox.cn/411631105e0</a>
     */
    suspend fun getImage(file: String): NapCatResponse<ImageInfo>

    /**
     * 发送群 AI 语音
     * @param groupId 群号
     * @param characterId 角色ID
     * @param text 文本
     * @see <a href="https://napcat.apifox.cn/411631106e0">https://napcat.apifox.cn/411631106e0</a>
     */
    suspend fun sendGroupAiRecord(groupId: Long, characterId: String, text: String): NapCatResponse<AiRecordResponse>
}
