package src.config

import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import src.service.BotService


/**
 * #TelegramBotsApi added to spring context as well
 */
@Configuration
//@ConditionalOnProperty(prefix = "telegrambots", name = ["enabled"], havingValue = "true", matchIfMissing = true)
open class TelegramBotStarterConfiguration {
    @Bean
    @ConditionalOnMissingBean(TelegramBotsApi::class)
    @Throws(TelegramApiException::class)
    open fun telegramBotsApi(): TelegramBotsApi {
        return TelegramBotsApi(DefaultBotSession::class.java)
    }

    @Bean
    @ConditionalOnMissingBean
    open fun telegramBotInitializer(
        telegramBotsApi: TelegramBotsApi?,
        longPollingBots: ObjectProvider<List<LongPollingBot>>,
       // webHookBots: ObjectProvider<List<SpringWebhookBot>>
    ): TelegramBotInitializer {
        return TelegramBotInitializer(
            telegramBotsApi!!,
            longPollingBots.getIfAvailable { emptyList() })
           // webHookBots.getIfAvailable { emptyList() })
    }
}