package analyzer.service.vk

import analyzer.model.vk.VkUser
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Period


@Service
class VkUserStatisticService : StatisticService<VkUser> {

    private val female = 1
    private val male = 2

    override fun getAverageAge(users: List<VkUser>): Double {
        val now = LocalDate.now()
        var counter = 0.0
        var totalAge = 0.0
        users.forEach {
            if (it.bdate != null) {
                totalAge += Period.between(it.bdate, now).years
                counter++
            }
        }
        return totalAge / counter
    }

    //TODO use Stream
    override fun getTotalAge(users: List<VkUser>): Pair<Int, Int> {
        var totalAge = 0
        var counter = 0
        val now = LocalDate.now()
        users.forEach {
            if (it.bdate != null) {
                totalAge += Period.between(it.bdate, now).years
                counter++
            }
        }
        return Pair(totalAge, counter)
    }

    override fun getMaleCount(users: List<VkUser>): Int {
        return users.filter { it.sex == male }.size
    }

    override fun getFemaleCount(users: List<VkUser>): Int {
        return users.filter { it.sex == female }.size
    }


    override fun populateUserCities(users: List<VkUser>, cityUser: MutableMap<String, Int>) {
        users.forEach {
            if (it.city != null) {
                val city = it.city
                if (cityUser.containsKey(city)) {
                    val count = cityUser[city]!!.plus(1)
                    cityUser[city] = count
                } else {
                    cityUser.put(city, 1)
                }
            }
        }
    }

    override fun getUserTopCities(cityUser: Map<String, Int>, top: Int): Map<String, Int> {
        val result: LinkedHashMap<String, Int> = LinkedHashMap()

        val entries = cityUser.toList()
            .sortedBy { (key, value) -> value }
            .toMap().entries.toList()

        for (i in 0 until top) {
            val entry: Map.Entry<String, Int> = entries[entries.size - i - 1]
            result.put(entry.key, entry.value)
        }

        return result
    }

    fun populateUserAgeMap(users: List<VkUser>, ageMap: MutableMap<Int, Int>) {
        users.forEach {
            if (it.bdate != null) {
                val age = getAge(it.bdate)

            }


        }
    }

    private fun getAge(bDate: LocalDate): Int {
        return Period.between(bDate, LocalDate.now()).years
    }


}