package analyzer.service.vk

import analyzer.api.VkApi
import analyzer.model.vk.GroupMembers
import analyzer.model.vk.VkGroup
import analyzer.model.vk.VkPost
import analyzer.model.vk.VkUser
import analyzer.parsing.VkParser
import analyzer.request.RequestExecutor
import analyzer.utils.ParamsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VkService @Autowired constructor(
    private val requestExecutor: RequestExecutor,
    private val vkParser: VkParser
) {

    fun getGroupMembers(groupNameId: String, offset: Int = 1000): GroupMembers {
        val userIdsJson: String =
            requestExecutor.executePostRequest(
                VkApi.GET_GROUP_MEMBERS,
                listOf(
                    Pair("access_token", VkApi.TMP_TOKEN),
                    Pair("group_id", groupNameId),
                    Pair("v", VkApi.API_VERSION_531),
                    Pair("offset", offset.toString())
                )
            )
        return vkParser.parseGroupMembers(userIdsJson)
    }

    fun getUsers(fields: List<String>, userIds: List<Int>): List<VkUser> {
        val paramsForRequest: List<Pair<String, String>> = listOf(
            Pair("access_token", VkApi.TMP_TOKEN),
            Pair("v", VkApi.API_VERSION_531),
            Pair("fields", ParamsUtils.joinListToString(fields)),
            Pair("user_ids", ParamsUtils.joinListToString(userIds))
        )
        val userInfo = requestExecutor.executePostRequest(VkApi.GET_USER, paramsForRequest)
        return vkParser.parseVkUsers(userInfo)
    }

    fun getVkGroup(groupNameOrId: String): VkGroup {
        val groupJson =
            requestExecutor.executePostRequest(
                VkApi.GET_GROUP_BY_ID,
                listOf(Pair("access_token", VkApi.TMP_TOKEN), Pair("group_id", getIdOrName(groupNameOrId)), Pair("v", VkApi.API_VERSION_531))
            )
        val group = vkParser.parseVKGroup(groupJson)
        val members = getGroupMembers(groupNameOrId).totalMembers
        group.members = members
        return group
    }

    fun getGroupPosts(groupId: Int, groupNameId: String): List<VkPost> {
        val groupPostJson = requestExecutor.executePostRequest(
            VkApi.GET_GROUP_POSTS,
            listOf(
                Pair("v", VkApi.API_VERSION_531),
                Pair("access_token", VkApi.TMP_TOKEN),
                Pair("owner_id", (groupId * -1).toString()),
                Pair("domain", groupNameId)
            )
        )
        return vkParser.parsePosts(groupPostJson)
    }

    private fun getIdOrName(groupNameOrId: String): String {
        return try {
            (Integer.valueOf(groupNameOrId) * -1).toString()
        } catch (e: NumberFormatException) {
            groupNameOrId
        }
    }
}