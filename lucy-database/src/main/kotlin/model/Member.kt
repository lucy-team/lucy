package model

import org.jetbrains.exposed.sql.ResultRow
import table.Members
import java.util.*

data class Member(
    val id: UUID? = null,
    val discordId: Long,
    val username: String,
    val avatarUrl: String?,
    val name: String,
) {
    companion object {
        fun fromResultRow(r: ResultRow): Member = Member(r[Members.id].value,
            r[Members.discordId],
            r[Members.username],
            r[Members.avatarUrl],
            r[Members.name])
    }
}