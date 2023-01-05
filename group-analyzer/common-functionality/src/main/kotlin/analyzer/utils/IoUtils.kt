package analyzer.utils

import org.springframework.core.io.InputStreamResource
import java.io.BufferedReader
import java.io.InputStreamReader

class IoUtils {
    companion object {
        fun getStringRequest(resource: InputStreamResource, encoding: String = "UTF-8"): String {
            BufferedReader(InputStreamReader(resource.inputStream, encoding)).use { br ->
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line)
                    sb.append('\n')
                }
                return sb.toString()
            }
        }
    }
}