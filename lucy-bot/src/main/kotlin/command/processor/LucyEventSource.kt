package command.processor

import bot.Lucy
import com.gitlab.kordlib.kordx.commands.model.processor.EventSource
import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorContext
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance

class LucyEventSource(val lucy: Lucy) : EventSource<MessageCreateEvent> {

    override val context: ProcessorContext<MessageCreateEvent, *, *>
        get() = LucyContext

    override val events: Flow<MessageCreateEvent>
        get() = lucy.events.buffer(Channel.UNLIMITED).filterIsInstance()

}