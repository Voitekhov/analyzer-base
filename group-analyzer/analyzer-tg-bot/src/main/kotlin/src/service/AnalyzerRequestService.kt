package src.service

import analyzer.model.RequestForAnalyzer
import org.springframework.stereotype.Service

@Service
class AnalyzerRequestService {

    fun createRequestForAnalyzer(userId: Long, text: String): RequestForAnalyzer {
        return RequestForAnalyzer(userId, getGroupNameFormUrl(text), getMetrix(text))
    }

    private fun getGroupNameFormUrl(groupUrl: String): String {
        return groupUrl.subSequence(groupUrl.lastIndexOf('/') + 1, groupUrl.indexOf(" ")).toString()
    }

    private fun getMetrix(text: String): List<String> {
        val params = mutableListOf<String>()
        text.substring(text.indexOf(" ")).split(",").forEach {
            params.add(it.trim())
        }
        return params
    }
}