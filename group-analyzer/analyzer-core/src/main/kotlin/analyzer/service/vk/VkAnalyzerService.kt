package analyzer.service.vk

import analyzer.api.VkApi
import analyzer.model.AnalyzerReport
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
    private val vkParser: VkParser,
    private val vkUserStatisticService: VkUserStatisticService
) {

    val token =
        "vk1.a.HTrVJJtwGYk8uybXEloVj7mlSvlY1MmAlRaW-HwAkhUfEJeW2UnVzXJIy84vWJACrqiCrokpCo_tphlyhn8jnsxPfr_b98cIYaRqByxvu9Z86YXka5w0xfv7Wh6nY3unaSoW9edNfsTiJwxnrsYQCyKyj8Rb8bAzi9X8ggFyGXxGg9YDb7qHC7MDp8UXo2jp4F8sdCdDUeXa50OFIHqiUQ"


    fun createAnalyzeReport(groupId: String, params: List<String>): AnalyzerReport {
        var offset = 0
        val report = AnalyzerReport()
        val cityUser: MutableMap<String, Int> = mutableMapOf()
        var maleCount = 0
        var femaleCount = 0
        var totalAge = 0
        var notNullBDate = 0
        var groupMembers: GroupMembers

        do {
            groupMembers = getGroupMembers(groupId, offset)
            val ids: List<Int> = groupMembers.offsetUsersIds

            val users: List<VkUser> = getUsers(listOf("sex,city,country,bdate"), ids)
            vkUserStatisticService.populateUserCities(users, cityUser)
            femaleCount += vkUserStatisticService.getFemaleCount(users)
            maleCount += vkUserStatisticService.getMaleCount(users)
            val totalAgeCount = vkUserStatisticService.getTotalAge(users)
            totalAge += totalAgeCount.first
            notNullBDate += totalAgeCount.second
            offset += 1000
        } while (offset < groupMembers.totalMembers)

        report.membersCount = groupMembers.totalMembers.toLong()

        val vkGroup = getVkGroup(groupId)
        report.groupName = vkGroup.name
        report.imageUrl = vkGroup.photoUrl

        // считать для тех у кого др не null
        val averageAge = totalAge / notNullBDate
        createStatistic(averageAge, maleCount, femaleCount, cityUser, params, report)
        return report

    }

    private fun createStatistic(
        averageAge: Int,
        maleCount: Int,
        femaleCount: Int,
        cityUser: MutableMap<String, Int>,
        params: List<String>,
        report: AnalyzerReport
    ) {
        params.forEach {
            if (it == "average_age") {
                report.averageAge = averageAge
            }
            if (it == "male_counts") {
                report.countMale = maleCount.toLong()
            }
            if (it == "female_counts") {
                report.countFemale = femaleCount.toLong();
            }
            if (it.startsWith("top_")) {
                val top = Integer.valueOf(it.substring(it.indexOf("_") + 1, it.lastIndexOf("_")))
                report.topCities = vkUserStatisticService.getUserTopCities(cityUser, top)
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