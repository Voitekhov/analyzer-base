package analyzer.model.vk

data class VkPostStatistic(
    val views: Int,
    val loveRate: Double,
    val talkRate: Double,
    val amplificationRate: Double,
    val engagementRate: Double,
    val engagementRateByResearch: Double
)