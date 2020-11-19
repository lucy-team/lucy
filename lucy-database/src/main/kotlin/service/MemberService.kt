package service

import model.Member
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.*
import table.*
import util.exposed.newSuspendedTransaction
import java.util.*

class MemberService {

    suspend fun all(): List<Member> = newSuspendedTransaction {
        /*val query = (Guilds innerJoin GuildMemberTable innerJoin Members)
            .slice(Members.columns)
            .select { (Guilds.id eq guildId) and (Guilds.id eq GuildMemberTable.guild) and (GuildMemberTable.member eq Members.id) }*/
        MemberEntity.all().map(MemberEntity::asMember)
    }

    suspend fun new(guildId: Long, member: Member): Member = newSuspendedTransaction {
        MemberEntity.new(member.id) {
            username = member.username
            avatarUrl = member.avatarUrl
            name = member.name
            guilds = SizedCollection(GuildEntity[guildId])
        }.asMember()
    }

    suspend fun delete(id: Long) = newSuspendedTransaction {
        MemberEntity[id].delete()
    }

    suspend fun get(id: Long): Member = newSuspendedTransaction {
        MemberEntity[id].asMember()
    }

    suspend fun update(member: Member): Member = newSuspendedTransaction {
        val id = member.id

        MemberEntity[id].apply {
            username = member.username
            avatarUrl = member.avatarUrl
            name = member.name
        }.asMember()
    }
}