/*
package src.config

import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.bots.TelegramWebhookBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook
import org.telegram.telegrambots.meta.api.objects.Update


*/
/**
 * @author Ruben Bermudez
 * @version 1.0
 *//*

abstract class SpringWebhookBot : TelegramWebhookBot {
    var setWebhook: SetWebhook

    constructor(setWebhook: SetWebhook) : super() {
        this.setWebhook = setWebhook
    }

    constructor(options: DefaultBotOptions?, setWebhook: SetWebhook) : super(options) {
        this.setWebhook = setWebhook
    }

    inner class TestSpringWebhookBot : SpringWebhookBot {
        constructor(setWebhook: SetWebhook) : super(setWebhook) {}
        constructor(options: DefaultBotOptions?, setWebhook: SetWebhook) : super(options, setWebhook) {}

        override fun getBotUsername(): String? {
            return null
        }

        override fun getBotToken(): String? {
            return null
        }

        override fun onWebhookUpdateReceived(update: Update): BotApiMethod<*>? {
            return null
        }

        override fun getBotPath(): String? {
            return null
        }
    }
}*/
