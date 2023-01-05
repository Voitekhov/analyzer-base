package analyzer.utils

class ParamsUtils {
    companion object {
        fun joinListToString(list: List<Any>): String {
            val sb = StringBuilder()
            list.forEach {
                sb.append("$it,")
            }
            return sb.toString()
        }
    }
}