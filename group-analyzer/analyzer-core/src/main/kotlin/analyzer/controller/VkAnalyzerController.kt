package analyzer.controller


import analyzer.model.RequestForAnalyzer
import analyzer.model.StatisticReport
import analyzer.service.vk.VkAnalyzerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VkAnalyzerController @Autowired constructor(
    private var vkAnalyzerService: VkAnalyzerService
) {

    @PostMapping("/analyze/base")
    fun analyze(@RequestBody requestForAnalyzer: RequestForAnalyzer): StatisticReport {
        val result = vkAnalyzerService.createGroupBaseStatisticReport(requestForAnalyzer.groupNameId);
        return result
    }
}