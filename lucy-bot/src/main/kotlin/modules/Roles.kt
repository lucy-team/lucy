@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.HelpKey
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
        metaData[HelpKey] = "Despliega una lista con los roles actuales del servidor."

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
        metaData[HelpKey] = "[@miembro] [@role] Elimina un rol de un miembro del servidor."
        invoke(MemberArgument, RoleArgument) { member, role ->
            member.removeRole(role.id).awaitSingle()
            respond("El rol se ha a eliminado correctamente del miembro")
        }
    }

    command("delrole") {
        metaData[HelpKey] = "[@role] Elimina un rol del servidor."
        invoke(RoleArgument) { role ->
            role.delete().awaitSingle()
            respond("El rol se ha a eliminado correctamente")
        }

    }

    command("addrole") {
        metaData[HelpKey] = "[nombre] Agrega nuevo rol al servidor."

        invoke(StringArgument) { name ->
            val guild = guild.awaitSingle()
            guild.createRole {
                it.setName(name)
            }.awaitSingle()

            respond("El rol se ha a creado correctamente")
        }
    }

    command("giverole") {
        metaData[HelpKey] = "[@miembro] [@role] Asigna un rol a un miembro."

        invoke(MemberArgument, RoleArgument) { member, role ->
            member.addRole(role.id).awaitSingle()
            respond("El rol se ha a asignado correctamente al miembro")
        }
    }

    command("useroles") {
        metaData[HelpKey] = "[@miembro] Muestra los roles de un miembro."

        invoke(MemberArgument) { member ->
            val roles = member.roles.map { it.name }.collectList().awaitSingle()

            respondEmbed {
                setTitle("Roles actuales:")
                setDescription(roles.joinToString("\n"))
            }
        }
    }
}
