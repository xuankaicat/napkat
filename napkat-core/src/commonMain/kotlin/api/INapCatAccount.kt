package io.github.xuankaicat.napkat.core.api

/**
 * 账号接口
 *
 * 好友管理、个人信息、消息状态相关接口
 */
interface INapCatAccount {
    /**
     * 获取好友列表
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631071e0">https://napcat.apifox.cn/411631071e0</a>
     */
    suspend fun getFriendList(noCache: Boolean? = null): NapCatResponse<List<Friend>>

    /**
     * 获取陌生人信息
     * @param userId QQ号
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631093e0">https://napcat.apifox.cn/411631093e0</a>
     */
    suspend fun getStrangerInfo(userId: Long, noCache: Boolean? = null): NapCatResponse<StrangerInfo>

    /**
     * 获取登录号信息
     * @see <a href="https://napcat.apifox.cn/411631087e0">https://napcat.apifox.cn/411631087e0</a>
     */
    suspend fun getLoginInfo(): NapCatResponse<LoginInfo>

    /**
     * 设置好友备注
     * @param userId QQ号
     * @param remark 备注
     * @see <a href="https://napcat.apifox.cn/411631094e0">https://napcat.apifox.cn/411631094e0</a>
     */
    suspend fun setFriendRemark(userId: Long, remark: String): NapCatResponse<Unit>

    /**
     * 删除好友
     * @param userId QQ号
     * @see <a href="https://napcat.apifox.cn/411631074e0">https://napcat.apifox.cn/411631074e0</a>
     */
    suspend fun deleteFriend(userId: Long): NapCatResponse<Unit>

    /**
     * 设置在线状态
     * @param status 状态 (online, invisible, busy, away)
     * @param extStatus 扩展状态
     * @param battery 电量
     * @see <a href="https://napcat.apifox.cn/411631077e0">https://napcat.apifox.cn/411631077e0</a>
     */
    suspend fun setOnlineStatus(status: String, extStatus: Int? = null, battery: Int? = null): NapCatResponse<Unit>

    /**
     * 设置个性签名
     * @param longNick 签名内容
     * @see <a href="https://napcat.apifox.cn/411631081e0">https://napcat.apifox.cn/411631081e0</a>
     */
    suspend fun setSelfLongnick(longNick: String): NapCatResponse<Unit>

    /**
     * 设置QQ头像
     * @param file 图片文件路径/URL/Base64
     * @see <a href="https://napcat.apifox.cn/411631079e0">https://napcat.apifox.cn/411631079e0</a>
     */
    suspend fun setQqAvatar(file: String): NapCatResponse<Unit>

    /**
     * 获取最近会话
     * @param count 数量
     * @see <a href="https://napcat.apifox.cn/411631066e0">https://napcat.apifox.cn/411631066e0</a>
     */
    suspend fun getRecentContact(count: Int = 10): NapCatResponse<List<MessageData>>

    /**
     * 处理加好友请求
     * @param flag 请求 flag
     * @param approve 是否同意
     * @param remark 备注
     * @see <a href="https://napcat.apifox.cn/411631069e0">https://napcat.apifox.cn/411631069e0</a>
     */
    suspend fun setFriendAddRequest(flag: String, approve: Boolean = true, remark: String? = null): NapCatResponse<Unit>

    /**
     * 获取单向好友列表
     * @see <a href="https://napcat.apifox.cn/411631086e0">https://napcat.apifox.cn/411631086e0</a>
     */
    suspend fun getUnidirectionalFriendList(): NapCatResponse<List<UnidirectionalFriend>>

    /**
     * 获取带分组的好友列表
     * @see <a href="https://napcat.apifox.cn/411631072e0">https://napcat.apifox.cn/411631072e0</a>
     */
    suspend fun getFriendsWithCategory(): NapCatResponse<List<FriendCategory>>

    /**
     * 处理可疑好友申请
     * @param flag 请求 flag
     * @param approve 是否同意
     * @see <a href="https://napcat.apifox.cn/411631093e0">https://napcat.apifox.cn/411631093e0</a>
     */
    suspend fun setDoubtFriendsAddRequest(flag: String, approve: Boolean): NapCatResponse<Unit>

    /**
     * 获取可疑好友申请
     * @see <a href="https://napcat.apifox.cn/411631092e0">https://napcat.apifox.cn/411631092e0</a>
     */
    suspend fun getDoubtFriendsAddRequest(): NapCatResponse<List<StrangerInfo>>

    /**
     * 点赞
     * @param userId QQ号
     * @param times 次数
     * @see <a href="https://napcat.apifox.cn/411631068e0">https://napcat.apifox.cn/411631068e0</a>
     */
    suspend fun sendLike(userId: Long, times: Int = 1): NapCatResponse<Unit>

    /**
     * 标记群消息已读
     *
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631065e0">https://napcat.apifox.cn/411631065e0</a>
     */
    suspend fun markGroupMsgAsRead(groupId: String): NapCatResponse<Unit>

    /**
     * 标记私聊消息已读
     *
     * @param userId 对方QQ号
     * @see <a href="https://napcat.apifox.cn/411631064e0">https://napcat.apifox.cn/411631064e0</a>
     */
    suspend fun markPrivateMsgAsRead(userId: String): NapCatResponse<Unit>

    /**
     * 标记消息已读
     *
     * @param userId 对方QQ号
     * @param groupId 群号
     * @param messageId 消息ID
     * @see <a href="https://napcat.apifox.cn/411631063e0">https://napcat.apifox.cn/411631063e0</a>
     */
    suspend fun markMsgAsRead(userId: String? = null, groupId: String? = null, messageId: Long? = null): NapCatResponse<Unit>

    /**
     * 标记所有消息已读
     * @see <a href="https://napcat.apifox.cn/411631067e0">https://napcat.apifox.cn/411631067e0</a>
     */
    suspend fun markAllAsRead(): NapCatResponse<Unit>
}
