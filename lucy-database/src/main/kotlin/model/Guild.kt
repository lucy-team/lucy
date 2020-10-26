package model

import java.util.*

data class Guild(
    var id: UUID? = null,
    val discordId: Long,
    val ownedBy: Long,
    val iconUrl: String?,
    val name: String,
)