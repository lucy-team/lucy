package table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GuildMemberTable : IntIdTable(name = "guild_member") {
    val guild = reference("guild_id", Guilds, ReferenceOption.CASCADE)
    val member = reference("member_id", Members, ReferenceOption.CASCADE)
}