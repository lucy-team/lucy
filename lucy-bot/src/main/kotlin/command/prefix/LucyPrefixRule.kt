package command.prefix

import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixBuilder
import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixRule
import discord4j.core.event.domain.message.MessageCreateEvent

fun PrefixBuilder.db(): PrefixRule<MessageCreateEvent> = PrefixRule { message, _ ->
    val prefix = "lt:" // TODO: Conectar a la base de datos para verificar el prefijo
    //println("Prefijo: ${prefix}")
    if (message.startsWith(prefix)) PrefixRule.Result.Accepted(prefix)
    else PrefixRule.Result.Denied
}