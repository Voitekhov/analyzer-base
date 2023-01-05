package src.controller

import analyzer.model.AnalyzerReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import src.service.AnalyzerRequestService

@RestController
class AnalyzerController {
    @Autowired
    lateinit var analyzerRequestService: AnalyzerRequestService

    @GetMapping
    fun getReport(@RequestBody report: AnalyzerReport) {
        // отправить его боту
    }

    @GetMapping("/test")
    fun test() {
        // отправить его боту
        analyzerRequestService.createRequestForAnalyzer(1L, "https://vk.com/likeoleg")
    }
}