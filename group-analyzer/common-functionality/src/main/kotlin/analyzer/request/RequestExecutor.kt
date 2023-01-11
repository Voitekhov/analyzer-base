package analyzer.request

import analyzer.utils.IoUtils
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.client.WebClient


@Component
class RequestExecutor @Autowired constructor(val webClient: WebClient) {

 /*   fun executePostRequest(api: String, params: List<Pair<String, String>>): String {
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
    }*/

    fun executePostRequest(api: String, params: List<Pair<String, String>>): String {
        val map = multiMapOf(params)
        val request =
            webClient.post()
                .uri { uriBuilder ->
                    uriBuilder.path(api)
                        .queryParams(map).build()
                }
                .retrieve()
                .bodyToMono(String::class.java).block()
        return request
    }

    private fun <K, V> multiMapOf(pairs: List<Pair<K, V>>): MultiValueMap<K, V> {
        val map: MultiValueMap<K, V> = LinkedMultiValueMap()
        pairs.forEach { map.add(it.first, it.second) }
        return map
    }
}