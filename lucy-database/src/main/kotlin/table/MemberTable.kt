package table

import model.Member
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Members : UUIDTable(name = "member") {
    val discordId = long("discord_id").uniqueIndex()
    val username = text("username")
    val avatarUrl = text("avatar_url").nullable()
    val name = text("name")
}

class MemberEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<MemberEntity>(Members)

    var discordId by Members.discordId
    var username by Members.username
    var avatarUrl by Members.avatarUrl
    var name by Members.name
    var guilds by GuildEntity via GuildMemberTable

    fun asMember() = Member(id.value, discordId, username, avatarUrl, name)
}

