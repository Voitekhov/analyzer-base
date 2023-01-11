package analyzer.service.vk

import analyzer.model.StatisticReport
import analyzer.model.vk.GroupMembers
import analyzer.model.vk.VkUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VkAnalyzerService @Autowired constructor(
    private val vkService: VkService
) {

    fun createGroupBaseStatisticReport(groupNameId: String): StatisticReport {
        var offset = 0
        val vkUserStatisticService = VkUserStatisticService()
        var groupMembers: GroupMembers

        do {
            groupMembers = vkService.getGroupMembers(groupNameId, offset)
            val ids: List<Int> = groupMembers.offsetUsersIds
            val users: List<VkUser> = vkService.getUsers(listOf("sex,city,country,bdate"), ids)

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

        val vkGroup = vkService.getVkGroup(groupNameId)
        statisticReport.groupName = vkGroup.name
        statisticReport.imageUrl = vkGroup.photoUrl

        return statisticReport

    }

}