@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.argument.MemberArgument
import command.argument.RoleArgument
import command.module.module
import command.respondEmbed
import discord4j.rest.util.Color
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

fun rolCommands() = module("rol") {

    command("roles") {
        invoke {
            val guild = guild.awaitSingle()
            val roles = guild.roles.map { it.name }.collectList().awaitSingle()

            respondEmbed {
                setTitle("Roles actuales:")
                setDescription(roles.joinToString("\n"))
            }
        }
    }

    command("rolepersist") {
        invoke(MemberArgument, RoleArgument) { member, role ->
            member.removeRole(role.id).awaitSingle()
        }
    }

    command("delrole") {
        invoke(RoleArgument) { role ->
            role.delete().awaitSingle()
        }
    }

    command("addrole") {
        invoke(StringArgument) { name ->
            val guild = guild.awaitSingle()
            guild.createRole {
                it.setName(name)
            }.awaitSingle()
        }
    }

    command("giverole") {
        invoke(MemberArgument, RoleArgument) { member, role ->
            member.addRole(role.id).awaitSingle()
        }
    }

    command("useroles") {
        invoke(MemberArgument) { member ->
            val roles = member.roles.map { it.name }.collectList().awaitSingle()

            respondEmbed {
                setTitle("Roles actuales:")
                setDescription(roles.joinToString("\n"))
            }
        }
    }
}
