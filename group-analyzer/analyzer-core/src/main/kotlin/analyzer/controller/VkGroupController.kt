package analyzer.controller

import analyzer.model.vk.VkGroup
import analyzer.model.vk.VkPost
import analyzer.service.vk.VkPostService
import analyzer.service.vk.VkService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vk/group/")
class VkGroupController
@Autowired constructor(private val vkService: VkService, private val vkPostService: VkPostService) {

    @GetMapping("{groupNameOrId}")
    fun getGroupByName(@PathVariable groupNameOrId: String): VkGroup {
        return vkService.getVkGroup(groupNameOrId)
    }

    @GetMapping("{groupNameOrId}/posts")
    fun getGroupPosts(@PathVariable groupNameOrId: String): List<VkPost> {
        return vkPostService.getPosts(groupNameOrId)
    }

}