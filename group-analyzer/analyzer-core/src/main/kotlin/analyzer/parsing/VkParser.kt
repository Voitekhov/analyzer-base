package analyzer.parsing


import analyzer.model.vk.*
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class VkParser {
    private val mapper = jacksonObjectMapper()

    fun parseGroupMembers(json: String): GroupMembers {
        val node = mapper.readTree(json);
        val objectNode = node["response"] as ObjectNode
        val count = objectNode["count"].asInt()
        val iterator = objectNode["items"].elements()
        val ids: MutableList<Int> = ArrayList()
        while (iterator.hasNext()) {
            ids.add(iterator.next().asInt())
        }
        return GroupMembers(count, ids)
    }

    fun parseVkUsers(json: String): List<VkUser> {
        val users: MutableList<VkUser> = ArrayList()
        val rootNode = mapper.readTree(json)
        val arrayNode = rootNode["response"] as ArrayNode
        val iterator = arrayNode.elements()
        while (iterator.hasNext()) {
            val userNode = iterator.next()
            users.add(parseVkUser(userNode))
        }
        return users
    }

    private fun parseVkUser(vkUserJson: JsonNode): VkUser {
        val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        val id = vkUserJson["id"].asInt()
        var bdate: LocalDate? = null
        bdate = try {
            if (vkUserJson["bdate"] != null) LocalDate.parse(vkUserJson["bdate"].asText(), formatter) else null
        } catch (e: Exception) {
            null
        }
        val sex = vkUserJson["sex"]?.asInt()
        val firstName = vkUserJson["first_name"].asText()
        val lastName = vkUserJson["last_name"].asText()
        val city = if (vkUserJson["city"] != null) vkUserJson["city"]["title"].asText() else null
        val country = if (vkUserJson["country"] != null) vkUserJson["country"]["title"].asText() else null
        val user = VkUser(id, bdate, city, country, sex, firstName, lastName)
        return user
    }

    fun parseVKGroup(json: String): VkGroup {
        val rootNode = mapper.readTree(json)
        val mainChild = ((rootNode["response"] as ArrayNode).get(0) as ObjectNode)
        val groupId = mainChild["id"].asInt()
        val groupName = mainChild["name"].asText()
        val avatarUrl = mainChild["photo_200"].asText()
        val nameId = mainChild["screen_name"].asText()
        return VkGroup(groupId, groupName, nameId, null, avatarUrl)
    }

    fun parsePosts(json: String): List<VkPost> {
        val posts: MutableList<VkPost> = ArrayList()
        val rootNode = mapper.readTree(json)
        val items = rootNode["response"].get("items") as ArrayNode
        items.forEach { jn ->
            run {
                posts.add(parsePost(jn))
            }
        }
        return posts
    }

    private fun parsePost(jsnNode: JsonNode): VkPost {
        val id = jsnNode["id"].asInt()
        val fromId = jsnNode["from_id"].asInt()
        val date = jsnNode["date"].asInt()
        val commentsCount = (jsnNode["comments"] as ObjectNode)["count"].asInt()
        val photos = getPhotos((jsnNode["attachments"] as ArrayNode))
        val likesCount = (jsnNode["likes"] as ObjectNode)["count"].asInt()
        val repostsCount = (jsnNode["reposts"] as ObjectNode)["count"].asInt()
        val text = jsnNode["text"].asText()
        val viewsCount = (jsnNode["views"] as ObjectNode)["count"].asInt()
        return VkPost(id, fromId, date, commentsCount, photos, likesCount, repostsCount, text, viewsCount)
    }

    private fun getPhotos(attachments: ArrayNode): List<VkPhoto> {
        val photos: MutableList<VkPhoto> = ArrayList()
        attachments.forEach { p ->
            if (p["type"].asText().equals("photo")) {
                val photoNode = p["photo"] as ObjectNode
                val id = photoNode["id"].asInt()
                val ownerId = photoNode["owner_id"].asInt()
                val url = getMaxSizePhotoUrl(photoNode["sizes"] as ArrayNode)
                val photo = VkPhoto(id, ownerId, url)
                photos.add(photo)
            }
        }
        return photos
    }

    private fun getMaxSizePhotoUrl(sizes: ArrayNode): String {
        val maxSize = sizes.lastOrNull() ?: return ""
        return maxSize.get("url").asText()
    }

}