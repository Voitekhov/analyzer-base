package analyzer.conf

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit


@Configuration

open class WebClientConfiguration {
    @Bean
    open fun webClientWithTimeout(): WebClient {
        val httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
            .responseTimeout(Duration.ofMillis(TIMEOUT.toLong()))
            .doOnConnected { conn: Connection ->
                conn.addHandlerLast(ReadTimeoutHandler(TIMEOUT.toLong(), TimeUnit.MILLISECONDS))
                    .addHandlerLast(WriteTimeoutHandler(TIMEOUT.toLong(), TimeUnit.MILLISECONDS))
            }

        val client = WebClient.builder().baseUrl("https://api.vk.com/method/")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
        return client
    }

    companion object {
        const val TIMEOUT: Int = 1000000
    }

}