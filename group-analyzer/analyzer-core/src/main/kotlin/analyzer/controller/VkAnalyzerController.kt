package analyzer.controller


import analyzer.model.AnalyzerReport
import analyzer.model.RequestForAnalyzer
import analyzer.service.vk.VkAnalyzerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VkAnalyzerController @Autowired constructor(
    private var vkAnalyzerService: VkAnalyzerService
) {

    @PostMapping("/analyze")
    fun analyze(@RequestBody requestForAnalyzer: RequestForAnalyzer): AnalyzerReport {
        val result = vkAnalyzerService.createAnalyzeReport(requestForAnalyzer.groupName, requestForAnalyzer.metrix);
        return result
    }
}