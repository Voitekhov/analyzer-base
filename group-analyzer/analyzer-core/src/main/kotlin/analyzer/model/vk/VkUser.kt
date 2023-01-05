package analyzer.model.vk

import java.time.LocalDate

// https://api.vk.com/method/users.get
data class VkUser(
    private val id: Int,
    val bdate: LocalDate?,
    val city: String?,
    private val country: String?,
    val sex: Int?,
    private val firstName: String,
    private val secondName: String
)