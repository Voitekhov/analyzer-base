package analyzer.service.vk

import analyzer.api.VkApi
import analyzer.model.StatisticReport
import analyzer.model.vk.GroupMembers
import analyzer.model.vk.VkGroup
import analyzer.model.vk.VkUser
import analyzer.parsing.VkParser
import analyzer.request.RequestExecutor
import analyzer.utils.ParamsUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VkAnalyzerService @Autowired constructor(
    private val requestExecutor: RequestExecutor,
    private val vkParser: VkParser
) {

    val token =
        "vk1.a.HTrVJJtwGYk8uybXEloVj7mlSvlY1MmAlRaW-HwAkhUfEJeW2UnVzXJIy84vWJACrqiCrokpCo_tphlyhn8jnsxPfr_b98cIYaRqByxvu9Z86YXka5w0xfv7Wh6nY3unaSoW9edNfsTiJwxnrsYQCyKyj8Rb8bAzi9X8ggFyGXxGg9YDb7qHC7MDp8UXo2jp4F8sdCdDUeXa50OFIHqiUQ"


    fun createAnalyzeReport(groupId: String, params: List<String>): StatisticReport {
        var offset = 0
        val vkUserStatisticService = VkUserStatisticService()
        var groupMembers: GroupMembers

        do {
            groupMembers = getGroupMembers(groupId, offset)
            val ids: List<Int> = groupMembers.offsetUsersIds
            val users: List<VkUser> = getUsers(listOf("sex,city,country,bdate"), ids)

            vkUserStatisticService.populateUserCities(users)
            vkUserStatisticService.increaseFemaleCount(users)
            vkUserStatisticService.increaseTotalAge(users)
            vkUserStatisticService.increaseMaleCount(users)
            vkUserStatisticService.populateUserAgeMap(users)
            vkUserStatisticService.populateUserCities(users)

            offset += 1000
        } while (offset < groupMembers.totalMembers)

        val statisticReport = vkUserStatisticService.getStatisticReport()
        statisticReport.membersCount = groupMembers.totalMembers

        val vkGroup = getVkGroup(groupId)
        statisticReport.groupName = vkGroup.name
        statisticReport.imageUrl = vkGroup.photoUrl

        return statisticReport

    }

    private fun createStatistic(
        averageAge: Int,
        maleCount: Int,
        femaleCount: Int,
        cityUser: MutableMap<String, Int>,
        params: List<String>,
        report: StatisticReport
    ) {
        params.forEach {
            if (it == "average_age") {
                report.averageAge = averageAge
            }
            if (it == "male_counts") {
                report.countMale = maleCount
            }
            if (it == "female_counts") {
                report.countFemale = femaleCount
            }
            if (it.startsWith("top_")) {
                val top = Integer.valueOf(it.substring(it.indexOf("_") + 1, it.lastIndexOf("_")))
               // report.topCities = vkUserStatisticService.getUserTopCities(cityUser, top)
            }
        }
    }

    private fun getGroupMembers(groupId: String, offset: Int = 1000): GroupMembers {
        val userIdsJson: String =
            requestExecutor.executePostRequest(
                VkApi.GET_GROUP_MEMBERS,
                listOf(
                    Pair("access_token", token),
                    Pair("group_id", groupId),
                    Pair("v", VkApi.API_VERSION_531),
                    Pair("offset", offset.toString())
                )
            )
        return vkParser.parseGroupMembers(userIdsJson)
    }

    private fun getUsers(fields: List<String>, userIds: List<Int>): List<VkUser> {
        val paramsForRequest: List<Pair<String, String>> = listOf(
            Pair("access_token", token),
            Pair("v", VkApi.API_VERSION_531),
            Pair("fields", ParamsUtils.joinListToString(fields)),
            Pair("user_ids", ParamsUtils.joinListToString(userIds))
        )
        val userInfo = requestExecutor.executePostRequest(VkApi.GET_USER, paramsForRequest)
        return vkParser.parseVkUsers(userInfo)
    }

    private fun getVkGroup(groupId: String): VkGroup {
        val groupJson =
            requestExecutor.executePostRequest(
                VkApi.GET_GROUP_BY_ID,
                listOf(Pair("access_token", token), Pair("group_id", groupId), Pair("v", VkApi.API_VERSION_531))
            )
        return vkParser.parseVKGroup(groupJson)
    }

}