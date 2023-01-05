package analyzer.parsing


import analyzer.model.vk.GroupMembers
import analyzer.model.vk.VkGroup
import analyzer.model.vk.VkUser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
class VkParser {
    val mapper = jacksonObjectMapper()

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

/*    fun <T : Any> deserializeJson(json: String, classToCast: KClass<T>): List<T> {
        val result: ArrayList<T> = ArrayList()
        when (classToCast) {
            VkUser::class -> {
                val parsed: List<VkUser> = mapper.readValue(json,object : TypeReference<List<VkUser>>(){})
                return parsed as List<T>
            }
            VkGroup::class -> {
                result.add(mapper.readValue(json, VkUser::class.java) as T)
            }
        }
        return result
    }*/

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

    fun parseVkUser(vkUserJson: JsonNode): VkUser {
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
        return VkGroup(groupId, groupName, null, avatarUrl)
    }

}