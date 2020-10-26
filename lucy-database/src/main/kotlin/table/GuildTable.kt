package table

import model.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object Guilds : UUIDTable(name = "guild") {
    val discordId = long("discord_id").uniqueIndex()
    val name = text("name")
    val iconUrl = text("icon_url").nullable()
    val ownedBy = long("owned_by")
}

class GuildEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<GuildEntity>(Guilds)

    var discordId by Guilds.discordId
    var iconUrl by Guilds.iconUrl
    var ownedBy by Guilds.ownedBy
    var name by Guilds.name
    var members by MemberEntity via GuildMemberTable


    fun asGuild() = Guild(id.value, discordId, ownedBy, iconUrl, name)
}

