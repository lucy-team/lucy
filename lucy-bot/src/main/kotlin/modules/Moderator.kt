@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.HelpKey
import command.argument.MemberArgument
import command.module.module
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle

fun moderatorCommands() = module("moderator") {

    command("kick") {
        metaData[HelpKey] = "[@miembro] Elimina un miembro del servidor."

        invoke(MemberArgument) { member ->
            respond("El miembro ha sido expulsado.")
            member.kick().awaitFirst()
        }
    }

    command("ban") {
        metaData[HelpKey] = "[@miembro] Banea a un miembro del servidor."

        invoke(MemberArgument) { member ->
            respond("El miembro ha sido baneado.")

            member.ban {
                it.setDeleteMessageDays(7)
            }.awaitFirst()
        }
    }

    command("unban") {
        metaData[HelpKey] = "[@miembro] Desbanea a un miembro del servidor."

        invoke(MemberArgument) { member ->
            respond("El miembro ha sido desbaneado.")
            member.unban().awaitFirst()
        }
    }

    command("mute") {
        metaData[HelpKey] = "[@miembro] Mutea a un miembro que se encuentre en un canal de voz."

        invoke(MemberArgument) { member ->
            member.edit {
                it.setMute(true)
            }.awaitFirst()
        }
    }

    command("unmute") {
        metaData[HelpKey] = "[@miembro] Desmutea a un miembro que se encuentre en un canal de voz."

        invoke(MemberArgument) { member ->
            member.edit {
                it.setMute(false)
            }.awaitFirst()
        }
    }

    command("purge") {
        alias("prune")

        metaData[HelpKey] = "[numero (cantidad)] Elimina una cantidad de mensajes del canal de texto."

        invoke(IntArgument) { cant ->
            if (cant < 0 || cant > 10000) {
                respond("La cantidad de mensajes a eliminar deber ser mayor a 0 y menor a 10000")
                return@invoke
            }

            val channelId = channel.awaitFirst().id.asLong()
            val messages = client.restClient.channelService.getMessages(channelId, mapOf("limit" to (cant + 1)))

            messages.flatMap { client.restClient.channelService.deleteMessage(channelId, it.id().toLong(), null) }
                .awaitSingle()
        }
    }
}