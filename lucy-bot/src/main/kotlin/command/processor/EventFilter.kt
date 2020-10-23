package command.processor

import com.gitlab.kordlib.kordx.commands.model.eventFilter.EventFilter
import discord4j.core.event.domain.message.MessageCreateEvent

fun eventFilter(
    predicate: suspend MessageCreateEvent.() -> Boolean
): EventFilter<MessageCreateEvent> =
    com.gitlab.kordlib.kordx.commands.model.eventFilter.eventFilter(LucyContext, predicate)