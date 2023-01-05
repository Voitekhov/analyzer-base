package src.request

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import analyzer.model.AnalyzerReport
import analyzer.model.RequestForAnalyzer
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Component
import analyzer.utils.IoUtils

@Component
class RequestSender {

    val objectWriter: ObjectWriter = ObjectMapper().writer()
    val objectMapper: ObjectMapper = ObjectMapper()

    fun sendToVkAnalyzer(requestForAnalyzer: RequestForAnalyzer): AnalyzerReport? {
        val client = HttpClients.createDefault()
        // вынести в отдельный метод по формированию дефолтного запроса
        val request = HttpPost("http://localhost:8080/analyze")
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-type", "application/json");
        val entity = StringEntity(objectWriter.writeValueAsString(requestForAnalyzer))
        request.entity = entity
        val response = client.execute(request)
        val json = IoUtils.getStringRequest(InputStreamResource(response.entity.content))
        return objectMapper.readValue(json, AnalyzerReport::class.java)
    }

    fun getPhotoResource(url: String): InputStreamResource {
        val client = HttpClients.createDefault()
        val request = HttpGet(url)
        return InputStreamResource(client.execute(request).entity.content)

    }
}