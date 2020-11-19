@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.argument.MemberArgument
import command.module.module
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle

fun moderatorCommands() = module("moderator") {

    command("kick") {
        invoke(MemberArgument) { member ->
            member.kick().awaitFirst()
        }
    }

    command("ban") {
        invoke(MemberArgument) { member ->
            member.ban {
                it.setDeleteMessageDays(7)
            }.awaitFirst()
        }
    }

    command("unban") {
        invoke(MemberArgument) { member ->
            member.unban().awaitFirst()
        }
    }

    command("mute") {
        invoke(MemberArgument) { member ->
            member.edit {
                it.setMute(true)
            }.awaitFirst()
        }
    }

    command("unmute") {
        invoke(MemberArgument) { member ->
            member.edit {
                it.setMute(false)
            }.awaitFirst()
        }
    }

    command("purge") {
        alias("prune")

        invoke(IntArgument) { cant ->
            val channelId = channel.awaitFirst().id.asLong()
            val messages = client.restClient.channelService.getMessages(channelId, mapOf("limit" to (cant + 1)))

            messages.flatMap { client.restClient.channelService.deleteMessage(channelId, it.id().toLong(), null) }
                .awaitSingle()
        }
    }
}