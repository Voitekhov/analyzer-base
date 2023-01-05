package analyzer.parsing.deserializer

import analyzer.model.vk.VkUser
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class VkUsersDeserializer(vc: Class<*>?) : StdDeserializer<List<VkUser>>(vc) {

    // better to use specific deserializer for every user in loop
    //TODO do not work with needed type
    override fun deserialize(jsonParser: JsonParser, ctxt: DeserializationContext?): List<VkUser> {
        val users: MutableList<VkUser> = ArrayList()
        val rootNode = jsonParser.codec.readTree<JsonNode>(jsonParser)
        val arrayNode = rootNode["response"] as ArrayNode
        val iterator = arrayNode.elements()
        val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
        while (iterator.hasNext()) {
            val userNode = iterator.next()
            val id = userNode["id"].asInt()
            var bdate: LocalDate? = null
            bdate = try {
                if (userNode["bdate"] != null) LocalDate.parse(userNode["bdate"].asText(), formatter) else null
            } catch (e: Exception) {
                null
            }
            val sex = if (userNode["sex"] != null) userNode["sex"].asInt() else null
            val firstName = userNode["first_name"].asText()
            val lastName = userNode["last_name"].asText()
            val city = if (userNode["city"] != null) userNode["city"]["title"].asText() else null
            val country = if (userNode["country"] != null) userNode["country"]["title"].asText() else null
            val user = VkUser(id, bdate, city, country, sex, firstName, lastName)
            users.add(user)
        }
        return users
    }
}