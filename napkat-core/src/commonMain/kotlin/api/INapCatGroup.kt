package io.github.xuankaicat.napkat.core.api

/**
 * 群组接口
 *
 * 群组管理、成员管理相关接口
 */
interface INapCatGroup {
    /**
     * 获取群列表
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631160e0">https://napcat.apifox.cn/411631160e0</a>
     */
    suspend fun getGroupList(noCache: Boolean? = null): NapCatResponse<List<GroupInfo>>

    /**
     * 获取群信息
     * @param groupId 群号
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631159e0">https://napcat.apifox.cn/411631159e0</a>
     */
    suspend fun getGroupInfo(groupId: Long, noCache: Boolean? = null): NapCatResponse<GroupInfo>

    /**
     * 获取群成员列表
     * @param groupId 群号
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631162e0">https://napcat.apifox.cn/411631162e0</a>
     */
    suspend fun getGroupMemberList(groupId: Long, noCache: Boolean? = null): NapCatResponse<List<GroupMemberInfo>>

    /**
     * 获取群成员信息
     * @param groupId 群号
     * @param userId QQ号
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631161e0">https://napcat.apifox.cn/411631161e0</a>
     */
    suspend fun getGroupMemberInfo(groupId: Long, userId: Long, noCache: Boolean? = null): NapCatResponse<GroupMemberInfo>

    /**
     * 设置群名称
     * @param groupId 群号
     * @param groupName 群名称
     * @see <a href="https://napcat.apifox.cn/411631151e0">https://napcat.apifox.cn/411631151e0</a>
     */
    suspend fun setGroupName(groupId: Long, groupName: String): NapCatResponse<Unit>

    /**
     * 设置群名片
     * @param groupId 群号
     * @param userId QQ号
     * @param card 群名片
     * @see <a href="https://napcat.apifox.cn/411631149e0">https://napcat.apifox.cn/411631149e0</a>
     */
    suspend fun setGroupCard(groupId: Long, userId: Long, card: String? = null): NapCatResponse<Unit>

    /**
     * 设置群头像
     * @param groupId 群号
     * @param file 图片文件
     * @see <a href="https://napcat.apifox.cn/411631147e0">https://napcat.apifox.cn/411631147e0</a>
     */
    suspend fun setGroupPortrait(groupId: Long, file: String): NapCatResponse<Unit>

    /**
     * 退出群组
     * @param groupId 群号
     * @param isDismiss 是否解散 (仅群主可用)
     * @see <a href="https://napcat.apifox.cn/411631154e0">https://napcat.apifox.cn/411631154e0</a>
     */
    suspend fun setGroupLeave(groupId: Long, isDismiss: Boolean = false): NapCatResponse<Unit>

    /**
     * 群组踢人
     * @param groupId 群号
     * @param userId QQ号
     * @param rejectAddRequest 是否拒绝再次申请
     * @see <a href="https://napcat.apifox.cn/411631142e0">https://napcat.apifox.cn/411631142e0</a>
     */
    suspend fun setGroupKick(groupId: Long, userId: Long, rejectAddRequest: Boolean = false): NapCatResponse<Unit>

    /**
     * 群组单人禁言
     * @param groupId 群号
     * @param userId QQ号
     * @param duration 禁言时长(秒), 0 为解除
     * @see <a href="https://napcat.apifox.cn/411631144e0">https://napcat.apifox.cn/411631144e0</a>
     */
    suspend fun setGroupBan(groupId: Long, userId: Long, duration: Int = 30 * 60): NapCatResponse<Unit>

    /**
     * 群组全员禁言
     * @param groupId 群号
     * @param enable 是否开启
     * @see <a href="https://napcat.apifox.cn/411631146e0">https://napcat.apifox.cn/411631146e0</a>
     */
    suspend fun setGroupWholeBan(groupId: Long, enable: Boolean = true): NapCatResponse<Unit>

    /**
     * 设置群管理员
     * @param groupId 群号
     * @param userId QQ号
     * @param enable 是否设置
     * @see <a href="https://napcat.apifox.cn/411631148e0">https://napcat.apifox.cn/411631148e0</a>
     */
    suspend fun setGroupAdmin(groupId: Long, userId: Long, enable: Boolean = true): NapCatResponse<Unit>

    /**
     * 处理加群请求
     * @param flag 请求 flag
     * @param subType 子类型 (add/invite)
     * @param approve 是否同意
     * @param reason 拒绝理由
     * @see <a href="https://napcat.apifox.cn/411631158e0">https://napcat.apifox.cn/411631158e0</a>
     */
    suspend fun setGroupAddRequest(flag: String, subType: String, approve: Boolean = true, reason: String? = null): NapCatResponse<Unit>

    /**
     * 获取群荣誉信息
     * @param groupId 群号
     * @param type 类型
     * @see <a href="https://napcat.apifox.cn/411631163e0">https://napcat.apifox.cn/411631163e0</a>
     */
    suspend fun getGroupHonorInfo(groupId: Long, type: String): NapCatResponse<GroupHonorInfo>

    /**
     * 获取群公告
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631157e0">https://napcat.apifox.cn/411631157e0</a>
     */
    suspend fun getGroupNotice(groupId: Long): NapCatResponse<List<Map<String, String>>>

    /**
     * 发送群公告
     * @param groupId 群号
     * @param content 公告内容
     * @see <a href="https://napcat.apifox.cn/411631155e0">https://napcat.apifox.cn/411631155e0</a>
     */
    suspend fun sendGroupNotice(groupId: Long, content: String): NapCatResponse<Unit>

    /**
     * 群打卡
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631168e0">https://napcat.apifox.cn/411631168e0</a>
     */
    suspend fun setGroupSign(groupId: Long): NapCatResponse<Unit>

    /**
     * 设置专属头衔
     * @param groupId 群号
     * @param userId QQ号
     * @param specialTitle 头衔
     * @param duration 有效期
     * @see <a href="https://napcat.apifox.cn/411631140e0">https://napcat.apifox.cn/411631140e0</a>
     */
    suspend fun setGroupSpecialTitle(groupId: Long, userId: Long, specialTitle: String? = null, duration: Int = -1): NapCatResponse<Unit>

    /**
     * 获取群被忽略的通知
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631167e0">https://napcat.apifox.cn/411631167e0</a>
     */
    suspend fun getGroupIgnoredNotifies(groupId: Long): NapCatResponse<Unit>

    /**
     * 获取群被忽略的加群请求
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631167e0">https://napcat.apifox.cn/411631167e0</a>
     */
    suspend fun getGroupIgnoreAddRequest(groupId: Long): NapCatResponse<Unit>

    /**
     * 批量踢出群成员
     * @param groupId 群号
     * @param userIds QQ号列表
     * @param rejectAddRequest 是否拒绝再次申请
     * @see <a href="https://napcat.apifox.cn/411631138e0">https://napcat.apifox.cn/411631138e0</a>
     */
    suspend fun setGroupKickMembers(groupId: Long, userIds: List<Long>, rejectAddRequest: Boolean = false): NapCatResponse<Unit>

    /**
     * 获取群禁言列表
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631166e0">https://napcat.apifox.cn/411631166e0</a>
     */
    suspend fun getGroupShutList(groupId: Long): NapCatResponse<List<Long>>

    /**
     * 获取群精华消息列表
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631145e0">https://napcat.apifox.cn/411631145e0</a>
     */
    suspend fun getEssenceMsgList(groupId: Long): NapCatResponse<List<EssenceMsg>>

    /**
     * 设置精华消息
     * @param messageId 消息ID
     * @see <a href="https://napcat.apifox.cn/411631150e0">https://napcat.apifox.cn/411631150e0</a>
     */
    suspend fun setEssenceMsg(messageId: Long): NapCatResponse<Unit>

    /**
     * 移出精华消息
     * @param messageId 消息ID
     * @see <a href="https://napcat.apifox.cn/411631152e0">https://napcat.apifox.cn/411631152e0</a>
     */
    suspend fun deleteEssenceMsg(messageId: Long): NapCatResponse<Unit>

    /**
     * 获取群系统消息
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631143e0">https://napcat.apifox.cn/411631143e0</a>
     */
    suspend fun getGroupSystemMsg(groupId: Long): NapCatResponse<Unit> // Return type?

    /**
     * 获取群 @全体成员 剩余次数
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631165e0">https://napcat.apifox.cn/411631165e0</a>
     */
    suspend fun getGroupAtAllRemain(groupId: Long): NapCatResponse<Map<String, Any>> // Return type?

    /**
     * 获取群过滤系统消息
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631167e0">https://napcat.apifox.cn/411631167e0</a>
     */
    suspend fun getGroupFilterSystemMsg(groupId: Long): NapCatResponse<Unit> // Return type?
}
