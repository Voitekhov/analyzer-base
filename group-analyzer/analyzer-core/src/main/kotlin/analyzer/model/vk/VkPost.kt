package analyzer.model.vk

data class VkPost(
    val id: Int,
    val fromId: Int,
    val date: Int,
    val commentsCount: Int,
    val photos: List<VkPhoto>,
    val likesCount: Int,
    val repostsCount: Int,
    val text: String,
    val viewsCount: Int,
    var vkPostStatistic: VkPostStatistic? = null
)