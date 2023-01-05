package analyzer.model

data class AnalyzerReport(
    var groupName: String? = null,
    var imageUrl: String? = null,
    var membersCount: Long? = null,
    var countMale: Long? = null,
    var countFemale: Long? = null,
    var averageAge: Int? = null,
    var topCities: Map<String, Int>? = null
) {
}