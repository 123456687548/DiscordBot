package data.bbk

data class Area(
    val areaDesc: String,
    val geocode: List<Geocode>,
    val polygon: List<String>
)