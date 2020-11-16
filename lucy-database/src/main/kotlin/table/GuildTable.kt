package table

import model.*
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object Guilds : LongIdTable(name = "guild") {
    val name = text("name")
    val iconUrl = text("icon_url").nullable()
    val ownedBy = long("owned_by")
}

class GuildEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GuildEntity>(Guilds)

    var iconUrl by Guilds.iconUrl
    var ownedBy by Guilds.ownedBy
    var name by Guilds.name
    var members by MemberEntity via GuildMemberTable


    fun asGuild() = Guild(id.value, ownedBy, iconUrl, name)
}

