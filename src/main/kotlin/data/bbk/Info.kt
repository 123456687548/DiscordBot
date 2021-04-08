package data.bbk

data class Info(
    val area: List<Area>,
    val category: List<String>,
    val certainty: String,
    val description: String,
    val event: String,
    val eventCode: List<EventCode>,
    val expires: String,
    val headline: String,
    val instruction: String,
    val web: String,
    val language: String,
    val parameter: List<Parameter>,
    val severity: String,
    val urgency: String
)