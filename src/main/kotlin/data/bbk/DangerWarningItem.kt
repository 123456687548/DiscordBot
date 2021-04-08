package data.bbk

data class DangerWarningItem(
    val code: List<String>,
    val identifier: String,
    val info: List<Info>,
    val msgType: String,
    val references: String,
    val scope: String,
    val sender: String,
    val sent: String,
    val status: String
)