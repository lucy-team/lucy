package command.processor

import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorContext
import discord4j.core.event.domain.message.MessageCreateEvent
import command.context.LucyCommandEvent

interface LucyContext: ProcessorContext<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent> {
    companion object: LucyContext
}
