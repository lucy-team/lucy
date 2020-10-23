package command.processor

import com.gitlab.kordlib.kordx.commands.model.processor.CommandEventData
import com.gitlab.kordlib.kordx.commands.model.processor.ContextConverter
import com.gitlab.kordlib.kordx.commands.model.processor.ErrorHandler
import discord4j.core.event.domain.message.MessageCreateEvent
import command.context.LucyCommandEvent

object LucyContextConverter: ContextConverter<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent> {
    override val MessageCreateEvent.text: String get() = message.content

    override fun MessageCreateEvent.toArgumentContext(): MessageCreateEvent = this

    override fun MessageCreateEvent.toCommandEvent(data: CommandEventData<LucyCommandEvent>): LucyCommandEvent {
        return LucyCommandEvent(this, data.command, data.commands, data.koin, data.processor)
    }

}

class LucyErrorHandler(
    private val suggester: CommandSuggester = CommandSuggester.Levenshtein
) : ErrorHandler<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent> {

}