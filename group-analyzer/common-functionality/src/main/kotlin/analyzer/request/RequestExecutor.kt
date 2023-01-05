package analyzer.request

import analyzer.utils.IoUtils
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Component

@Component
class RequestExecutor {
    fun executePostRequest(api: String, params: List<Pair<String, String>>): String {
        // optimize
        val client = HttpClients.createDefault()
        val requestParams: ArrayList<NameValuePair> = ArrayList()
        val request = HttpPost(api)
        for (p in params) {
            requestParams.add(BasicNameValuePair(p.first, p.second))
        }
        request.entity = UrlEncodedFormEntity(requestParams)
        val isr = InputStreamResource(client.execute(request).entity.content)
        val json: String = IoUtils.getStringRequest(isr, "UTF-8")
        return json
    }
}