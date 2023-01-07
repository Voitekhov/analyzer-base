package src.service

import analyzer.model.StatisticReport
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import src.request.RequestSender

@Component
class BotService : TelegramLongPollingBot() {

    @Autowired
    private lateinit var analyzerRequestService: AnalyzerRequestService

    @Autowired
    private lateinit var requestSender: RequestSender

    val description =
        "specify a link to the group and the required metrics, separated by commas. example, https://vk.com/groupname average_age, member_counts, male_counts, female_counts, top_{3}_cities"

    override fun getBotToken(): String {
        return "5685009391:AAGwQMiBNDpIwhes0TveE7zJz88ct0ysLKU"
    }

    override fun getBotUsername(): String {
        return "analyzer_hse_bot"
    }

    override fun onUpdateReceived(update: Update?) {
        val message = update!!.message
        val userId = message.from.id
        val text = message.text
        when (text) {
            "/description" -> sendText(userId, description)
            else -> {
                val requestForAnalyzer = analyzerRequestService.createRequestForAnalyzer(userId, text)
                val report = requestSender.sendToVkAnalyzer(requestForAnalyzer)
                sendReport(report!!, userId)
            }
        }
    }

    fun sendText(userId: Long, text: String) {
        val sendMessage: SendMessage = SendMessage.builder().chatId(userId.toString()).text(text).build()
        execute(sendMessage)
    }

    fun sendReport(report: StatisticReport, userId: Long) {
        val sendPhoto =
            SendPhoto.builder().chatId(userId.toString()).photo(InputFile(requestSender.getPhotoResource(report.imageUrl!!).inputStream, "asd"))
                .caption(createStringReport(report)).build();
        execute(sendPhoto)
    }

    private fun createStringReport(report: StatisticReport): String {
        val sb = StringBuilder()
        sb.append("Group name: ${report.groupName} \n")
        sb.append("Total members: ${report.membersCount} \n")
        if (report.averageAge != null) {
            sb.append("Average age: ${report.averageAge} \n")
        }
        if (report.countFemale != null) {
            sb.append("Female count: ${report.countFemale} \n")
        }
        if (report.countMale != null) {
            sb.append("Male count: ${report.countMale} \n")
        }
        if (report.cityUser != null) {
            var number = 1;
            sb.append("Top cities: \n")
            for (e in report.cityUser!!) {
                sb.append("$number . ${e.key} ${e.value} \n")
                number++
            }
        }
        return sb.toString()
    }
}