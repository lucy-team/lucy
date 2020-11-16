package model

data class Guild(
    var id: Long,
    val ownedBy: Long,
    val iconUrl: String?,
    val name: String,
)