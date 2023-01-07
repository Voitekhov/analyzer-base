package analyzer.service.vk

interface StatisticService<T> {
    fun calculateAverageAge(users: List<T>)
    fun getTotalAge(users: List<T>)
    fun getMaleCount(users: List<T>): Int
    fun getFemaleCount(users: List<T>): Int
    fun populateUserCities(users: List<T>)
    fun getUserTopCities(top: Int = 3): Map<String, Int>
}