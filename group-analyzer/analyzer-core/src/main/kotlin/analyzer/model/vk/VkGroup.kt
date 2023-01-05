package analyzer.model.vk

// https://api.vk.com/method/groups.getById
data class VkGroup(val id: Int, val name: String, val memberCount: Int?, val photoUrl: String?)