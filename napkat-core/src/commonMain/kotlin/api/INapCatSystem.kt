package io.github.xuankaicat.napkat.core.api

/**
 * 系统接口
 *
 * 状态获取、重启、缓存清理相关接口
 */
interface INapCatSystem {
    /**
     * 获取Packet状态
     * @see <a href="https://napcat.apifox.cn/411631189e0">https://napcat.apifox.cn/411631189e0</a>
     */
    suspend fun ncGetPacketStatus(): NapCatResponse<PacketStatus>

    /**
     * 发送原始数据包
     * @param data 数据包
     * @see <a href="https://napcat.apifox.cn/411631190e0">https://napcat.apifox.cn/411631190e0</a>
     */
    suspend fun sendPacket(data: String): NapCatResponse<Unit>

    /**
     * 退出登录
     * @see <a href="https://napcat.apifox.cn/411631192e0">https://napcat.apifox.cn/411631192e0</a>
     */
    suspend fun botExit(): NapCatResponse<Unit>

    /**
     * 获取机器人 UIN 范围
     * @see <a href="https://napcat.apifox.cn/411631191e0">https://napcat.apifox.cn/411631191e0</a>
     */
    suspend fun getRobotUinRange(): NapCatResponse<List<RobotUinRange>>

    /**
     * 获取版本信息
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun getVersionInfo(): NapCatResponse<VersionInfo>

    /**
     * 获取运行状态
     * @see <a href="https://napcat.apifox.cn/411631088e0">https://napcat.apifox.cn/411631088e0</a>
     */
    suspend fun getStatus(): NapCatResponse<Status>

    /**
     * 重启服务
     * @param delay 延迟毫秒
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun setRestart(delay: Int = 0): NapCatResponse<Unit>

    /**
     * 清理缓存
     */
    suspend fun cleanCache(): NapCatResponse<Unit>

    /**
     * 图片 OCR
     * @param imageID 图片ID
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun ocrImage(imageID: String): NapCatResponse<OcrResult>

    /**
     * 英文翻译
     * @param words 单词
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun translateEn2Zh(words: List<String>): NapCatResponse<List<String>>

    /**
     * 获取在线客户端
     * @param noCache 是否不使用缓存
     * @see <a href="https://napcat.apifox.cn/411631089e0">https://napcat.apifox.cn/411631089e0</a>
     */
    suspend fun getOnlineClients(noCache: Boolean? = null): NapCatResponse<List<Device>>

    /**
     * 检查 URL 安全性
     * @param url URL
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun checkUrlSafely(url: String): NapCatResponse<Int>

    /**
     * 获取机型显示
     * @param model 机型
     * @see <a href="https://napcat.apifox.cn/411631090e0">https://napcat.apifox.cn/411631090e0</a>
     */
    suspend fun getModelShow(model: String): NapCatResponse<List<Map<String, String>>>

    /**
     * 设置机型显示
     * @param model 机型
     * @param show 显示
     * @see <a href="https://napcat.apifox.cn/411631091e0">https://napcat.apifox.cn/411631091e0</a>
     */
    suspend fun setModelShow(model: String, show: String): NapCatResponse<Unit>

    /**
     * 是否可以发送图片
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun canSendImage(): NapCatResponse<Boolean>

    /**
     * 是否可以发送语音
     * @see <a href="https://napcat.apifox.cn/411631188e0">https://napcat.apifox.cn/411631188e0</a>
     */
    suspend fun canSendRecord(): NapCatResponse<Boolean>
}
