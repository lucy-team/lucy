package command.prefix

import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixBuilder
import com.gitlab.kordlib.kordx.commands.model.prefix.PrefixRule
import discord4j.core.event.domain.message.MessageCreateEvent
import command.processor.LucyContext

inline fun PrefixBuilder.lucy(supplier: () -> PrefixRule<MessageCreateEvent>) = add(LucyContext, supplier())

fun PrefixBuilder.lucy(supplier: PrefixRule<MessageCreateEvent>) = add(LucyContext, supplier)