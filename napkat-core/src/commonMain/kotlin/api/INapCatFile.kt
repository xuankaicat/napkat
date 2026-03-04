package io.github.xuankaicat.napkat.core.api

/**
 * 文件接口
 *
 * 文件上传下载、预览相关接口
 */
interface INapCatFile {
    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @see <a href="https://napcat.apifox.cn/411631204e0">https://napcat.apifox.cn/411631204e0</a>
     */
    suspend fun getFile(fileId: String): NapCatResponse<FileInfo>

    /**
     * 上传群文件
     * @param groupId 群号
     * @param file 文件路径
     * @param name 文件名
     * @param folder 父目录ID
     * @see <a href="https://napcat.apifox.cn/411631199e0">https://napcat.apifox.cn/411631199e0</a>
     */
    suspend fun uploadGroupFile(groupId: Long, file: String, name: String, folder: String? = null): NapCatResponse<Unit>

    /**
     * 上传私聊文件
     * @param userId QQ号
     * @param file 文件路径
     * @param name 文件名
     * @see <a href="https://napcat.apifox.cn/411631200e0">https://napcat.apifox.cn/411631200e0</a>
     */
    suspend fun uploadPrivateFile(userId: Long, file: String, name: String): NapCatResponse<Unit>

    /**
     * 删除群文件
     * @param groupId 群号
     * @param fileId 文件ID
     * @param busid busid
     * @see <a href="https://napcat.apifox.cn/411631208e0">https://napcat.apifox.cn/411631208e0</a>
     */
    suspend fun deleteGroupFile(groupId: Long, fileId: String, busid: Int): NapCatResponse<Unit>

    /**
     * 创建群文件夹
     * @param groupId 群号
     * @param name 文件夹名
     * @param parentId 父目录ID
     * @see <a href="https://napcat.apifox.cn/411631207e0">https://napcat.apifox.cn/411631207e0</a>
     */
    suspend fun createGroupFileFolder(groupId: Long, name: String, parentId: String = "/"): NapCatResponse<Unit>

    /**
     * 删除群文件夹
     * @param groupId 群号
     * @param folderId 文件夹ID
     * @see <a href="https://napcat.apifox.cn/411631209e0">https://napcat.apifox.cn/411631209e0</a>
     */
    suspend fun deleteGroupFolder(groupId: Long, folderId: String): NapCatResponse<Unit>

    /**
     * 获取群文件系统信息
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631203e0">https://napcat.apifox.cn/411631203e0</a>
     */
    suspend fun getGroupFileSystemInfo(groupId: Long): NapCatResponse<GroupFileSystemInfo>

    /**
     * 获取群文件列表
     * @param groupId 群号
     * @param folderId 文件夹ID
     * @see <a href="https://napcat.apifox.cn/411631202e0">https://napcat.apifox.cn/411631202e0</a>
     */
    suspend fun getGroupFilesByFolder(groupId: Long, folderId: String): NapCatResponse<GroupFilesResponse>

    /**
     * 获取群根目录文件列表
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631201e0">https://napcat.apifox.cn/411631201e0</a>
     */
    suspend fun getGroupRootFiles(groupId: Long): NapCatResponse<GroupFilesResponse>

    /**
     * 获取群文件下载链接
     * @param groupId 群号
     * @param fileId 文件ID
     * @param busid busid
     * @see <a href="https://napcat.apifox.cn/411631205e0">https://napcat.apifox.cn/411631205e0</a>
     */
    suspend fun getGroupFileUrl(groupId: Long, fileId: String, busid: Int): NapCatResponse<String>

    /**
     * 获取私聊文件下载链接
     * @param userId QQ号
     * @param fileId 文件ID
     * @see <a href="https://napcat.apifox.cn/411631206e0">https://napcat.apifox.cn/411631206e0</a>
     */
    suspend fun getPrivateFileUrl(userId: Long, fileId: String): NapCatResponse<String>

    /**
     * 移动群文件
     * @param groupId 群号
     * @param fileId 文件ID
     * @param folderId 目标文件夹ID
     * @see <a href="https://napcat.apifox.cn/411631210e0">https://napcat.apifox.cn/411631210e0</a>
     */
    suspend fun moveGroupFile(groupId: Long, fileId: String, folderId: String): NapCatResponse<Unit>

    /**
     * 重命名群文件
     * @param groupId 群号
     * @param fileId 文件ID
     * @param name 新文件名
     * @see <a href="https://napcat.apifox.cn/411631211e0">https://napcat.apifox.cn/411631211e0</a>
     */
    suspend fun renameGroupFile(groupId: Long, fileId: String, name: String): NapCatResponse<Unit>

    /**
     * 转存为永久文件
     * @param fileId 文件ID
     * @see <a href="https://napcat.apifox.cn/411631212e0">https://napcat.apifox.cn/411631212e0</a>
     */
    suspend fun transGroupFileToPermanent(groupId: Long, fileId: String, busid: Int): NapCatResponse<Unit>

    /**
     * 下载文件到缓存目录
     * @param url 文件URL
     * @param name 文件名
     * @param base64 Base64内容
     * @see <a href="https://napcat.apifox.cn/411631213e0">https://napcat.apifox.cn/411631213e0</a>
     */
    suspend fun downloadFile(url: String? = null, name: String? = null, base64: String? = null): NapCatResponse<String>

    /**
     * 清理缓存
     * @see <a href="https://napcat.apifox.cn/411631214e0">https://napcat.apifox.cn/411631214e0</a>
     */
    suspend fun cleanCache(): NapCatResponse<Unit>

    /**
     * 删除群相册文件
     * @param groupId 群号
     * @param albumId 相册ID
     * @param batchIdList 批次ID列表
     * @see <a href="https://napcat.apifox.cn/411631215e0">https://napcat.apifox.cn/411631215e0</a>
     */
    suspend fun deleteGroupAlbumMedia(groupId: Long, albumId: String, batchIdList: List<String>): NapCatResponse<Unit>

    /**
     * 点赞群相册
     * @param groupId 群号
     * @param albumId 相册ID
     * @see <a href="https://napcat.apifox.cn/411631216e0">https://napcat.apifox.cn/411631216e0</a>
     */
    suspend fun setGroupAlbumMediaLike(groupId: Long, albumId: String): NapCatResponse<Unit>

    /**
     * 查看群相册评论
     * @param groupId 群号
     * @param albumId 相册ID
     * @see <a href="https://napcat.apifox.cn/411631217e0">https://napcat.apifox.cn/411631217e0</a>
     */
    suspend fun getGroupAlbumComment(groupId: Long, albumId: String): NapCatResponse<List<Map<String, String>>>

    /**
     * 获取群相册列表
     * @param groupId 群号
     * @see <a href="https://napcat.apifox.cn/411631218e0">https://napcat.apifox.cn/411631218e0</a>
     */
    suspend fun getGroupAlbumList(groupId: Long): NapCatResponse<List<Map<String, String>>>

    /**
     * 上传图片到群相册
     * @param groupId 群号
     * @param albumId 相册ID
     * @param file 图片文件
     * @see <a href="https://napcat.apifox.cn/411631219e0">https://napcat.apifox.cn/411631219e0</a>
     */
    suspend fun uploadImageToGroupAlbum(groupId: Long, albumId: String, file: String): NapCatResponse<Unit>

    /**
     * 获取群相册媒体列表
     * @param groupId 群号
     * @param albumId 相册ID
     * @see <a href="https://napcat.apifox.cn/411631220e0">https://napcat.apifox.cn/411631220e0</a>
     */
    suspend fun getGroupAlbumMediaList(groupId: Long, albumId: String): NapCatResponse<List<Map<String, String>>>
}
