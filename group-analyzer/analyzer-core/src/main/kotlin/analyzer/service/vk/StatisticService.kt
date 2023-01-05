package analyzer.service.vk

interface StatisticService<T> {
    fun getAverageAge(users: List<T>): Double
    fun getTotalAge(users: List<T>): Pair<Int, Int>
    fun getMaleCount(users: List<T>): Int
    fun getFemaleCount(users: List<T>): Int
    fun populateUserCities(users: List<T>, cityUser: MutableMap<String, Int>)
    fun getUserTopCities(cityUser: Map<String, Int>, top: Int = 3): Map<String, Int>
}