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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

fun rolCommands() = module("rol") {

    command("roles") {
        metaData[HelpKey] = "Despliega una lista con los roles actuales del servidor."

        invoke {
            guild.flatMap {
                it.roles.map { it.name }.collectList()
            }.asFlow().collect {
                respondEmbed {
                    setTitle("Roles actuales:")
                    setDescription(it.joinToString("\n"))
                }
            }
        }
    }

    command("rolepersist") {
        metaData[HelpKey] = "[@miembro] [@role] Elimina un rol de un miembro del servidor."
        invoke(MemberArgument, RoleArgument) { member, role ->
            respond("El rol se ha a eliminado correctamente del miembro")
            member.removeRole(role.id).awaitSingle()
        }
    }

    command("delrole") {
        metaData[HelpKey] = "[@role] Elimina un rol del servidor."

        invoke(RoleArgument) { role ->
            respond("El rol se ha a eliminado correctamente")
            role.delete().awaitSingle()
        }

    }

    command("addrole") {
        metaData[HelpKey] = "[nombre] Agrega nuevo rol al servidor."

        invoke(StringArgument) { name ->
            val guild = guild.awaitSingle()
            guild.createRole {
                it.setName(name)
            }.asFlow().collect {
                respond("El rol se ha a creado correctamente")
            }
        }
    }

    command("giverole") {
        metaData[HelpKey] = "[@miembro] [@role] Asigna un rol a un miembro."

        invoke(MemberArgument, RoleArgument) { member, role ->
            respond("El rol se ha a asignado correctamente al miembro")
            member.addRole(role.id).awaitSingle()
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
