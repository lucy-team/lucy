package command.prefix

import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixBuilder
import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixRule
import discord4j.core.`object`.entity.Guild
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitSingle

fun PrefixBuilder.db(): PrefixRule<MessageCreateEvent> = PrefixRule { message, ctx ->
    if (!ctx.message.guildId.isPresent) // Se verifica que el mensaje este en un servidor
        return@PrefixRule PrefixRule.Result.Denied

    val prefix = "lt:" // TODO: Conectar a la base de datos para verificar el prefijo
    if (message.startsWith(prefix)) PrefixRule.Result.Accepted(prefix)
    else PrefixRule.Result.Denied
}