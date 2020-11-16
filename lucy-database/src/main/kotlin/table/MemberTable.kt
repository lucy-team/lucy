package table

import model.Member
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Members : LongIdTable(name = "member") {
    val username = text("username")
    val avatarUrl = text("avatar_url").nullable()
    val name = text("name")
}

class MemberEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MemberEntity>(Members)

    var username by Members.username
    var avatarUrl by Members.avatarUrl
    var name by Members.name
    var guilds by GuildEntity via GuildMemberTable

    fun asMember() = Member(id.value, username, avatarUrl, name)
}

