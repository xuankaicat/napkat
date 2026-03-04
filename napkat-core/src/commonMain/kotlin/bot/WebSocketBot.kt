package io.github.xuankaicat.napkat.core.bot

import io.github.xuankaicat.napkat.core.api.*
import io.github.xuankaicat.napkat.core.event.*
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.*
import kotlin.random.Random
import kotlin.time.Clock

open class WebSocketBot(
    private val client: HttpClient,
    private val host: String,
    private val port: Int,
    private val token: String? = null
) : Bot {

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    private var session: DefaultClientWebSocketSession? = null
    private val pendingRequests = mutableMapOf<String, CompletableDeferred<NapCatResponse<JsonElement>>>()
    private val listeners = mutableListOf<NapCatEventListener>()

    open fun registerListener(listener: NapCatEventListener) {
        listeners.add(listener)
    }

    open suspend fun connect() {
        val url = if (token == null) "ws://$host:$port"
        else "ws://$host:$port?access_token=$token"

        client.webSocket(url) {
            session = this
            try {
                incoming.consumeEach { frame ->
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        handleMessage(text)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun handleMessage(text: String) {
        try {
            val element = json.parseToJsonElement(text).jsonObject
            if (element.containsKey("echo")) {
                val echo = element["echo"]?.jsonPrimitive?.content
                if (echo != null && pendingRequests.containsKey(echo)) {
                    val response = json.decodeFromJsonElement<NapCatResponse<JsonElement>>(element)
                    pendingRequests[echo]?.complete(response)
                    pendingRequests.remove(echo)
                }
            } else {
                handleEvent(element)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun handleEvent(element: JsonObject) {
        val postType = element["post_type"]?.jsonPrimitive?.content ?: return
        val event: NapCatEvent? = try {
            when (postType) {
                "message" -> {
                    when (element["message_type"]?.jsonPrimitive?.content) {
                        "group" -> json.decodeFromJsonElement<GroupMessageEvent>(element)
                        "private" -> json.decodeFromJsonElement<PrivateMessageEvent>(element)
                        else -> null
                    }
                }
                "notice" -> json.decodeFromJsonElement<NoticeEvent>(element)
                "request" -> json.decodeFromJsonElement<RequestEvent>(element)
                "meta_event" -> json.decodeFromJsonElement<MetaEvent>(element)
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        if (event != null) {
            listeners.forEach { it.onEvent(event) }
        }
    }

    private fun JsonObjectBuilder.putParam(key: String, value: Any?) {
        when (value) {
            null -> put(key, JsonNull)
            is String -> put(key, value)
            is Number -> put(key, value)
            is Boolean -> put(key, value)
            is IMessage -> put(key, json.encodeToJsonElement(IMessage.serializer(), value))
            is List<*> -> {
                val array = buildJsonArray {
                    value.forEach { item ->
                        when (item) {
                            is IMessage -> add(json.encodeToJsonElement(IMessage.serializer(), item))
                            is String -> add(item)
                            is Number -> add(item)
                            is Boolean -> add(item)
                            else -> add(item.toString())
                        }
                    }
                }
                put(key, array)
            }
            else -> put(key, value.toString())
        }
    }

    private suspend inline fun <reified T> callApi(action: String, params: JsonObject): NapCatResponse<T> {
        val echo = "${action}_${Clock.System.now().toEpochMilliseconds()}_${Random.nextInt(0, 1000)}"
        val deferred = CompletableDeferred<NapCatResponse<JsonElement>>()
        pendingRequests[echo] = deferred
        
        val request = buildJsonObject {
            put("action", action)
            put("params", params)
            put("echo", echo)
        }
        
        val jsonString = json.encodeToString(request)
        session?.send(Frame.Text(jsonString)) ?: throw IllegalStateException("Not connected")
        
        val response = deferred.await()
        
        val dataElement = response.data
        val data: T? = if (dataElement != null && dataElement !is JsonNull) {
             json.decodeFromJsonElement<T>(dataElement)
        } else {
            null
        }
        
        return NapCatResponse(
            status = response.status,
            retcode = response.retcode,
            data = data,
            message = response.message,
            wording = response.wording,
            stream = response.stream
        )
    }
    // region INapCatMessage
    override suspend fun sendGroupMsg(groupId: String, message: List<IMessage>): NapCatResponse<SendMsgResponse> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("message", message)
        }
        return callApi("send_group_msg", params)
    }

    override suspend fun sendGroupMsg(groupId: String, message: IMessage): NapCatResponse<SendMsgResponse> {
        return sendGroupMsg(groupId, listOf(message))
    }

    override suspend fun sendGroupMsg(groupId: String, message: String): NapCatResponse<SendMsgResponse> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("message", message)
        }
        return callApi("send_group_msg", params)
    }

    override suspend fun sendPrivateMsg(userId: String, message: List<IMessage>): NapCatResponse<SendMsgResponse> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("message", message)
        }
        return callApi("send_private_msg", params)
    }

    override suspend fun sendPrivateMsg(userId: String, message: IMessage): NapCatResponse<SendMsgResponse> {
        return sendPrivateMsg(userId, listOf(message))
    }

    override suspend fun sendPrivateMsg(userId: String, message: String): NapCatResponse<SendMsgResponse> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("message", message)
        }
        return callApi("send_private_msg", params)
    }

    override suspend fun sendPoke(userId: String, groupId: String?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("group_id", groupId)
        }
        val action = if (groupId != null) "group_poke" else "friend_poke"
        return callApi(action, params)
    }

    override suspend fun deleteMsg(messageId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("message_id", messageId) }
        return callApi("delete_msg", params)
    }

    override suspend fun getGroupMsgHistory(groupId: Long, messageSeq: Int?, count: Int?): NapCatResponse<List<MessageData>> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("message_seq", messageSeq)
            putParam("count", count)
        }
        return callApi("get_group_msg_history", params)
    }

    override suspend fun getMsg(messageId: Long): NapCatResponse<MessageData> {
        val params = buildJsonObject { putParam("message_id", messageId) }
        return callApi("get_msg", params)
    }

    override suspend fun getForwardMsg(messageId: String): NapCatResponse<ForwardMsgData> {
        val params = buildJsonObject { putParam("message_id", messageId) }
        return callApi("get_forward_msg", params)
    }

    override suspend fun setMsgEmojiLike(messageId: Long, emojiId: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("message_id", messageId)
            putParam("emoji_id", emojiId)
        }
        return callApi("set_msg_emoji_like", params)
    }

    override suspend fun getFriendMsgHistory(userId: Long, messageSeq: Int?, count: Int?): NapCatResponse<List<MessageData>> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("message_seq", messageSeq)
            putParam("count", count)
        }
        return callApi("get_friend_msg_history", params)
    }

    override suspend fun getEmojiLikes(messageId: String, emojiId: String): NapCatResponse<EmojiLikeList> {
        val params = buildJsonObject {
            putParam("message_id", messageId)
            putParam("emoji_id", emojiId)
        }
        return callApi("get_emoji_likes", params)
    }

    override suspend fun sendForwardMsg(messages: List<Any>): NapCatResponse<SendMsgResponse> {
        throw UnsupportedOperationException("Generic sendForwardMsg not supported without context")
    }

    override suspend fun getRecord(file: String, outFormat: String): NapCatResponse<RecordInfo> {
        val params = buildJsonObject {
            putParam("file", file)
            putParam("out_format", outFormat)
        }
        return callApi("get_record", params)
    }

    override suspend fun getImage(file: String): NapCatResponse<ImageInfo> {
        val params = buildJsonObject { putParam("file", file) }
        return callApi("get_image", params)
    }

    override suspend fun sendGroupAiRecord(groupId: Long, characterId: String, text: String): NapCatResponse<AiRecordResponse> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("character_id", characterId)
            putParam("text", text)
        }
        return callApi("send_group_ai_record", params)
    }
    // endregion INapCatMessage

    // region INapCatGroup
    override suspend fun getGroupList(noCache: Boolean?): NapCatResponse<List<GroupInfo>> {
        val params = buildJsonObject { putParam("no_cache", noCache) }
        return callApi("get_group_list", params)
    }

    override suspend fun getGroupInfo(groupId: Long, noCache: Boolean?): NapCatResponse<GroupInfo> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("no_cache", noCache)
        }
        return callApi("get_group_info", params)
    }

    override suspend fun getGroupMemberList(groupId: Long, noCache: Boolean?): NapCatResponse<List<GroupMemberInfo>> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("no_cache", noCache)
        }
        return callApi("get_group_member_list", params)
    }

    override suspend fun getGroupMemberInfo(groupId: Long, userId: Long, noCache: Boolean?): NapCatResponse<GroupMemberInfo> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_id", userId)
            putParam("no_cache", noCache)
        }
        return callApi("get_group_member_info", params)
    }

    override suspend fun setGroupName(groupId: Long, groupName: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("group_name", groupName)
        }
        return callApi("set_group_name", params)
    }

    override suspend fun setGroupCard(groupId: Long, userId: Long, card: String?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_id", userId)
            putParam("card", card)
        }
        return callApi("set_group_card", params)
    }

    override suspend fun setGroupPortrait(groupId: Long, file: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file", file)
        }
        return callApi("set_group_portrait", params)
    }

    override suspend fun setGroupLeave(groupId: Long, isDismiss: Boolean): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("is_dismiss", isDismiss)
        }
        return callApi("set_group_leave", params)
    }

    override suspend fun setGroupKick(groupId: Long, userId: Long, rejectAddRequest: Boolean): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_id", userId)
            putParam("reject_add_request", rejectAddRequest)
        }
        return callApi("set_group_kick", params)
    }

    override suspend fun setGroupBan(groupId: Long, userId: Long, duration: Int): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_id", userId)
            putParam("duration", duration)
        }
        return callApi("set_group_ban", params)
    }

    override suspend fun setGroupWholeBan(groupId: Long, enable: Boolean): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("enable", enable)
        }
        return callApi("set_group_whole_ban", params)
    }

    override suspend fun setGroupAdmin(groupId: Long, userId: Long, enable: Boolean): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_id", userId)
            putParam("enable", enable)
        }
        return callApi("set_group_admin", params)
    }

    override suspend fun setGroupAddRequest(flag: String, subType: String, approve: Boolean, reason: String?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("flag", flag)
            putParam("sub_type", subType)
            putParam("approve", approve)
            putParam("reason", reason)
        }
        return callApi("set_group_add_request", params)
    }

    override suspend fun getGroupHonorInfo(groupId: Long, type: String): NapCatResponse<GroupHonorInfo> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("type", type)
        }
        return callApi("get_group_honor_info", params)
    }

    override suspend fun getGroupNotice(groupId: Long): NapCatResponse<List<Map<String, String>>> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_notice", params)
    }

    override suspend fun sendGroupNotice(groupId: Long, content: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("content", content)
        }
        return callApi("send_group_notice", params)
    }

    override suspend fun setGroupSign(groupId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("set_group_sign", params)
    }

    override suspend fun setGroupSpecialTitle(groupId: Long, userId: Long, specialTitle: String?, duration: Int): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_id", userId)
            putParam("special_title", specialTitle)
            putParam("duration", duration)
        }
        return callApi("set_group_special_title", params)
    }

    override suspend fun getGroupIgnoredNotifies(groupId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_ignored_notifies", params)
    }

    override suspend fun getGroupIgnoreAddRequest(groupId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_ignore_add_request", params)
    }

    override suspend fun setGroupKickMembers(groupId: Long, userIds: List<Long>, rejectAddRequest: Boolean): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("user_ids", userIds)
            putParam("reject_add_request", rejectAddRequest)
        }
        return callApi("set_group_kick_members", params)
    }

    override suspend fun getGroupShutList(groupId: Long): NapCatResponse<List<Long>> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_shut_list", params)
    }

    override suspend fun getEssenceMsgList(groupId: Long): NapCatResponse<List<EssenceMsg>> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_essence_msg_list", params)
    }

    override suspend fun setEssenceMsg(messageId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("message_id", messageId) }
        return callApi("set_essence_msg", params)
    }

    override suspend fun deleteEssenceMsg(messageId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("message_id", messageId) }
        return callApi("delete_essence_msg", params)
    }

    override suspend fun getGroupSystemMsg(groupId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_system_msg", params)
    }

    override suspend fun getGroupAtAllRemain(groupId: Long): NapCatResponse<Map<String, Any>> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_at_all_remain", params)
    }

    override suspend fun getGroupFilterSystemMsg(groupId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_filter_system_msg", params)
    }
    // endregion INapCatGroup

    // region INapCatAccount
    override suspend fun getFriendList(noCache: Boolean?): NapCatResponse<List<Friend>> {
        val params = buildJsonObject { putParam("no_cache", noCache) }
        return callApi("get_friend_list", params)
    }

    override suspend fun getStrangerInfo(userId: Long, noCache: Boolean?): NapCatResponse<StrangerInfo> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("no_cache", noCache)
        }
        return callApi("get_stranger_info", params)
    }

    override suspend fun getLoginInfo(): NapCatResponse<LoginInfo> {
        return callApi("get_login_info", buildJsonObject {})
    }

    override suspend fun setFriendRemark(userId: Long, remark: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("remark", remark)
        }
        return callApi("set_friend_remark", params)
    }

    override suspend fun deleteFriend(userId: Long): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("user_id", userId) }
        return callApi("delete_friend", params)
    }

    override suspend fun setOnlineStatus(status: String, extStatus: Int?, battery: Int?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("status", status)
            putParam("ext_status", extStatus)
            putParam("battery", battery)
        }
        return callApi("set_online_status", params)
    }

    override suspend fun setSelfLongnick(longNick: String): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("long_nick", longNick) }
        return callApi("set_self_longnick", params)
    }

    override suspend fun setQqAvatar(file: String): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("file", file) }
        return callApi("set_qq_avatar", params)
    }

    override suspend fun getRecentContact(count: Int): NapCatResponse<List<MessageData>> {
        val params = buildJsonObject { putParam("count", count) }
        return callApi("get_recent_contact", params)
    }

    override suspend fun setFriendAddRequest(flag: String, approve: Boolean, remark: String?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("flag", flag)
            putParam("approve", approve)
            putParam("remark", remark)
        }
        return callApi("set_friend_add_request", params)
    }

    override suspend fun getUnidirectionalFriendList(): NapCatResponse<List<UnidirectionalFriend>> {
        return callApi("get_unidirectional_friend_list", buildJsonObject {})
    }

    override suspend fun getFriendsWithCategory(): NapCatResponse<List<FriendCategory>> {
        return callApi("get_friends_with_category", buildJsonObject {})
    }

    override suspend fun setDoubtFriendsAddRequest(flag: String, approve: Boolean): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("flag", flag)
            putParam("approve", approve)
        }
        return callApi("set_doubt_friends_add_request", params)
    }

    override suspend fun getDoubtFriendsAddRequest(): NapCatResponse<List<StrangerInfo>> {
        return callApi("get_doubt_friends_add_request", buildJsonObject {})
    }

    override suspend fun sendLike(userId: Long, times: Int): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("times", times)
        }
        return callApi("send_like", params)
    }

    override suspend fun markGroupMsgAsRead(groupId: String): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("mark_group_msg_as_read", params)
    }

    override suspend fun markPrivateMsgAsRead(userId: String): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("user_id", userId) }
        return callApi("mark_private_msg_as_read", params)
    }

    override suspend fun markMsgAsRead(userId: String?, groupId: String?, messageId: Long?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("group_id", groupId)
            putParam("message_id", messageId)
        }
        return callApi("mark_msg_as_read", params)
    }

    override suspend fun markAllAsRead(): NapCatResponse<Unit> {
        return callApi("mark_all_as_read", buildJsonObject {})
    }
    // endregion INapCatAccount

    // region INapCatFile
    override suspend fun getFile(fileId: String): NapCatResponse<FileInfo> {
        val params = buildJsonObject { putParam("file_id", fileId) }
        return callApi("get_file", params)
    }

    override suspend fun uploadGroupFile(groupId: Long, file: String, name: String, folder: String?): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file", file)
            putParam("name", name)
            putParam("folder", folder)
        }
        return callApi("upload_group_file", params)
    }

    override suspend fun uploadPrivateFile(userId: Long, file: String, name: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("file", file)
            putParam("name", name)
        }
        return callApi("upload_private_file", params)
    }

    override suspend fun deleteGroupFile(groupId: Long, fileId: String, busid: Int): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file_id", fileId)
            putParam("busid", busid)
        }
        return callApi("delete_group_file", params)
    }

    override suspend fun createGroupFileFolder(groupId: Long, name: String, parentId: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("name", name)
            putParam("parent_id", parentId)
        }
        return callApi("create_group_file_folder", params)
    }

    override suspend fun deleteGroupFolder(groupId: Long, folderId: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("folder_id", folderId)
        }
        return callApi("delete_group_folder", params)
    }

    override suspend fun getGroupFileSystemInfo(groupId: Long): NapCatResponse<GroupFileSystemInfo> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_file_system_info", params)
    }

    override suspend fun getGroupFilesByFolder(groupId: Long, folderId: String): NapCatResponse<GroupFilesResponse> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("folder_id", folderId)
        }
        return callApi("get_group_files_by_folder", params)
    }

    override suspend fun getGroupRootFiles(groupId: Long): NapCatResponse<GroupFilesResponse> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_root_files", params)
    }

    override suspend fun getGroupFileUrl(groupId: Long, fileId: String, busid: Int): NapCatResponse<String> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file_id", fileId)
            putParam("busid", busid)
        }
        return callApi("get_group_file_url", params)
    }

    override suspend fun getPrivateFileUrl(userId: Long, fileId: String): NapCatResponse<String> {
        val params = buildJsonObject {
            putParam("user_id", userId)
            putParam("file_id", fileId)
        }
        return callApi("get_private_file_url", params)
    }

    override suspend fun moveGroupFile(groupId: Long, fileId: String, folderId: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file_id", fileId)
            putParam("folder_id", folderId)
        }
        return callApi("move_group_file", params)
    }

    override suspend fun renameGroupFile(groupId: Long, fileId: String, name: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file_id", fileId)
            putParam("name", name)
        }
        return callApi("rename_group_file", params)
    }

    override suspend fun transGroupFileToPermanent(groupId: Long, fileId: String, busid: Int): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("file_id", fileId)
            putParam("busid", busid)
        }
        return callApi("trans_group_file_to_permanent", params)
    }

    override suspend fun downloadFile(url: String?, name: String?, base64: String?): NapCatResponse<String> {
        val params = buildJsonObject {
            putParam("url", url)
            putParam("name", name)
            putParam("base64", base64)
        }
        return callApi("download_file", params)
    }

    override suspend fun cleanCache(): NapCatResponse<Unit> {
        return callApi("clean_cache", buildJsonObject {})
    }

    override suspend fun deleteGroupAlbumMedia(groupId: Long, albumId: String, batchIdList: List<String>): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("album_id", albumId)
            putParam("batch_id_list", batchIdList)
        }
        return callApi("delete_group_album_media", params)
    }

    override suspend fun setGroupAlbumMediaLike(groupId: Long, albumId: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("album_id", albumId)
        }
        return callApi("set_group_album_media_like", params)
    }

    override suspend fun getGroupAlbumComment(groupId: Long, albumId: String): NapCatResponse<List<Map<String, String>>> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("album_id", albumId)
        }
        return callApi("get_group_album_comment", params)
    }

    override suspend fun getGroupAlbumList(groupId: Long): NapCatResponse<List<Map<String, String>>> {
        val params = buildJsonObject { putParam("group_id", groupId) }
        return callApi("get_group_album_list", params)
    }

    override suspend fun uploadImageToGroupAlbum(groupId: Long, albumId: String, file: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("album_id", albumId)
            putParam("file", file)
        }
        return callApi("upload_image_to_group_album", params)
    }

    override suspend fun getGroupAlbumMediaList(groupId: Long, albumId: String): NapCatResponse<List<Map<String, String>>> {
        val params = buildJsonObject {
            putParam("group_id", groupId)
            putParam("album_id", albumId)
        }
        return callApi("get_group_album_media_list", params)
    }
    // endregion INapCatFile

    // region INapCatSystem
    override suspend fun ncGetPacketStatus(): NapCatResponse<PacketStatus> {
        return callApi("nc_get_packet_status", buildJsonObject {})
    }

    override suspend fun sendPacket(data: String): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("data", data) }
        return callApi("send_packet", params)
    }

    override suspend fun botExit(): NapCatResponse<Unit> {
        return callApi("bot_exit", buildJsonObject {})
    }

    override suspend fun getRobotUinRange(): NapCatResponse<List<RobotUinRange>> {
        return callApi("get_robot_uin_range", buildJsonObject {})
    }

    override suspend fun getVersionInfo(): NapCatResponse<VersionInfo> {
        return callApi("get_version_info", buildJsonObject {})
    }

    override suspend fun getStatus(): NapCatResponse<Status> {
        return callApi("get_status", buildJsonObject {})
    }

    override suspend fun setRestart(delay: Int): NapCatResponse<Unit> {
        val params = buildJsonObject { putParam("delay", delay) }
        return callApi("set_restart", params)
    }

    override suspend fun ocrImage(imageID: String): NapCatResponse<OcrResult> {
        val params = buildJsonObject { putParam("imageID", imageID) }
        return callApi("ocr_image", params)
    }

    override suspend fun translateEn2Zh(words: List<String>): NapCatResponse<List<String>> {
        val params = buildJsonObject { putParam("words", words) }
        return callApi("translate_en2zh", params)
    }

    override suspend fun getOnlineClients(noCache: Boolean?): NapCatResponse<List<Device>> {
        val params = buildJsonObject { putParam("no_cache", noCache) }
        return callApi("get_online_clients", params)
    }

    override suspend fun checkUrlSafely(url: String): NapCatResponse<Int> {
        val params = buildJsonObject { putParam("url", url) }
        return callApi("check_url_safely", params)
    }

    override suspend fun getModelShow(model: String): NapCatResponse<List<Map<String, String>>> {
        val params = buildJsonObject { putParam("model", model) }
        return callApi("get_model_show", params)
    }

    override suspend fun setModelShow(model: String, show: String): NapCatResponse<Unit> {
        val params = buildJsonObject {
            putParam("model", model)
            putParam("show", show)
        }
        return callApi("set_model_show", params)
    }

    override suspend fun canSendImage(): NapCatResponse<Boolean> {
        return callApi("can_send_image", buildJsonObject {})
    }

    override suspend fun canSendRecord(): NapCatResponse<Boolean> {
        return callApi("can_send_record", buildJsonObject {})
    }

    // INapCatKey
    override suspend fun getCookies(domain: String): NapCatResponse<Cookies> {
        val params = buildJsonObject { putParam("domain", domain) }
        return callApi("get_cookies", params)
    }

    override suspend fun getCsrfToken(): NapCatResponse<CsrfToken> {
        return callApi("get_csrf_token", buildJsonObject {})
    }

    override suspend fun getCredentials(): NapCatResponse<Credentials> {
        return callApi("get_credentials", buildJsonObject {})
    }

    override suspend fun getRkey(): NapCatResponse<List<RKeyInfo>> {
        return callApi("get_rkey", buildJsonObject {})
    }

    override suspend fun getRkeyServer(): NapCatResponse<RKeyServerInfo> {
        return callApi("get_rkey_server", buildJsonObject {})
    }

    override suspend fun ncGetRkey(): NapCatResponse<List<NcRKeyInfo>> {
        return callApi("nc_get_rkey", buildJsonObject {})
    }

    override suspend fun getClientKey(): NapCatResponse<ClientKeyInfo> {
        return callApi("get_client_key", buildJsonObject {})
    }
    // endregion INapCatKey
}
