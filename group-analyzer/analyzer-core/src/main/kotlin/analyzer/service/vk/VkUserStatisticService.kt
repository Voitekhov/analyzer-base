package analyzer.service.vk

import analyzer.model.StatisticReport
import analyzer.model.vk.VkUser
import java.time.LocalDate
import java.time.Period


class VkUserStatisticService {

    private val female = 1
    private val male = 2

    private val report: StatisticReport = StatisticReport()
    private var totalAge: Int = 0
    private var isRangeReady = false

    private fun getAverageAge(): Int {
        return totalAge / report.notNullBDate
    }

    fun increaseTotalAge(users: List<VkUser>) {
        val now = LocalDate.now()
        users.forEach {
            if (it.bdate != null) {
                totalAge += getAge(it.bdate)
                report.notNullBDate++
            }
        }
    }

    fun increaseMaleCount(users: List<VkUser>) {
        val maleCount = users.filter { it.sex == male }.size
        report.countMale += maleCount
    }

    fun increaseFemaleCount(users: List<VkUser>) {
        val femaleCount = users.filter { it.sex == female }.size
        report.countFemale += femaleCount
    }


    fun populateUserCities(users: List<VkUser>) {
        users.forEach {
            if (it.city != null) {
                val city = it.city
                if (report.cityUser.containsKey(city)) {
                    val count = report.cityUser[city]!!.plus(1)
                    report.cityUser[city] = count
                } else {
                    report.cityUser.put(city, 1)
                }
            }
        }
    }

    fun getUserTopCities(top: Int): Map<String, Int> {
        return getUserTopCities(top, report.cityUser)
    }

    fun getUserTopCities(top: Int, cityUser: Map<String, Int>): Map<String, Int> {
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


    fun populateUserAgeMap(users: List<VkUser>) {
        if (!isRangeReady) {
            populateRange()
        }
        users.forEach {
            if (it.bdate != null) {
                val age = getAge(it.bdate)
                putValueToAgeMap(age)
            }
        }
    }

    private fun getAge(bDate: LocalDate): Int {
        return Period.between(bDate, LocalDate.now()).years
    }

    private fun populateRange() {
        report.ageMap[0] = 0
        report.ageMap[18] = 0
        report.ageMap[25] = 0
        report.ageMap[35] = 0
        report.ageMap[45] = 0
        report.ageMap[55] = 0
        isRangeReady = true
    }

    private fun putValueToAgeMap(age: Int) {
        fun merge(key: Int) {
            report.ageMap.merge(key, 1) { v1, v2 -> v1 + v2 }
        }
        if (age >= 55) {
            merge(55)
            return
        }
        if (age >= 45) {
            merge(45)
            return
        }
        if (age >= 35) {
            merge(35)
            return
        }
        if (age >= 25) {
            merge(25)
            return
        }
        if (age >= 18) {
            merge(18)
            return
        }
        if (age >= 0) {
            merge(0)
            return
        }
    }

    fun getStatisticReport(): StatisticReport {
        report.averageAge = getAverageAge()
        return report
    }
}