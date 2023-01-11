package analyzer.service.vk

import analyzer.model.vk.VkGroup
import analyzer.model.vk.VkPost
import analyzer.model.vk.VkPostStatistic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class VkPostService @Autowired constructor(private val vkService: VkService) {

    fun calculateStatisticForPost(post: VkPost): VkPostStatistic {
        val group = vkService.getVkGroup(post.fromId.toString())
        return calculateStatisticForPost(post, group)
    }

    fun calculateStatisticForPost(post: VkPost, group: VkGroup): VkPostStatistic {
        fun round(double: Double): Double {
            return Math.round(double * 1000.0) / 1000.0
        }

        val groupMembers = group.members!!
        val views = post.viewsCount
        val loveRate = round((post.likesCount.toDouble() / groupMembers) * 100)
        val talkRate = round((post.commentsCount.toDouble() / groupMembers) * 100)
        val amplificationRate = round((post.repostsCount.toDouble() / groupMembers) * 100)
        val engagementRate =
            round((post.commentsCount.toDouble() + post.likesCount.toDouble() + post.repostsCount.toDouble()) / groupMembers * 100)
        val engagementRateByResearch =
            round((post.commentsCount.toDouble() + post.likesCount.toDouble() + post.repostsCount.toDouble()) / post.viewsCount * 100)
        return VkPostStatistic(views, loveRate, talkRate, amplificationRate, engagementRate, engagementRateByResearch)
    }

    fun getPosts(groupIdOrName: String): List<VkPost> {
        val group = vkService.getVkGroup(groupIdOrName)
        val posts = vkService.getGroupPosts(group.id, group.groupNameId)
        posts.forEach { p ->
            p.vkPostStatistic = calculateStatisticForPost(p, group)
        }
        return posts
    }
}