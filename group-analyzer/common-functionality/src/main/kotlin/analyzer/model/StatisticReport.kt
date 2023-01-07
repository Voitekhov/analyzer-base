package analyzer.model

data class StatisticReport(
    var groupName: String? = null,
    var imageUrl: String? = null,
    var membersCount: Int = 0,
    var countMale: Int = 0,
    var countFemale: Int = 0,
    var averageAge: Int = 0,
    var notNullBDate: Int = 0,
    var cityUser: MutableMap<String, Int> = HashMap(),
    var ageMap: MutableMap<Int, Int> = HashMap()
) {
}