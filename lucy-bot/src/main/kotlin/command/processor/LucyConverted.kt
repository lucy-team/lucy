package command.processor

import com.gitlab.kordlib.kordx.commands.model.processor.CommandEventData
import com.gitlab.kordlib.kordx.commands.model.processor.CommandProcessor
import com.gitlab.kordlib.kordx.commands.model.processor.ContextConverter
import com.gitlab.kordlib.kordx.commands.model.processor.ErrorHandler
import command.HelpKey
import command.LucyEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import command.context.LucyCommandEvent
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle

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

    private val backtick = "`"
    private val backtickEscape = "\u200E`"

    private suspend inline fun respondError(
        event: MessageCreateEvent,
        text: String,
        characterIndex: Int,
        message: String
    ) {
        val parsedText = text.take(characterIndex)
        val lastNewlineIndex = parsedText.lastIndexOf("\n")
        val spacers = if (lastNewlineIndex > 0) characterIndex - lastNewlineIndex - 1 //\n itself
        else characterIndex

        event.message.channel
            .flatMap {
                it.createMessage("""
            <|>```
            <|>${text.replace(backtick, backtickEscape)}
            <|>${"-".repeat(spacers)}^ ${message.replace(backtick, backtickEscape)}
            <|>```
            """.trimMargin("<|>").trim())
            }
            .awaitSingle()
    }

    override suspend fun CommandProcessor.emptyInvocation(event: MessageCreateEvent) { /*ignored*/
    }

    override suspend fun CommandProcessor.notFound(event: MessageCreateEvent, command: String) {
        val mostProbable = suggester.suggest(command, commands)
        if (mostProbable == null) {
            event.message.channel
                .flatMap {
                    it.createMessage("$command este comando no existe")
                }
                .awaitSingle()
            return
        }

        event.message.channel
            .flatMap {
                it.createMessage("$command ese comando no existe, quizas sea ${mostProbable.name}?")
            }
            .awaitSingle()
    }

    override suspend fun CommandProcessor.rejectArgument(
        rejection: ErrorHandler.RejectedArgument<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent>
    ) = with(rejection) {
        respondError(event, eventText, atChar, "Falta el argumento ${this.argument.name}")
    }

    override suspend fun CommandProcessor.tooManyWords(
        rejection: ErrorHandler.TooManyWords<MessageCreateEvent, LucyCommandEvent>
    ) = with(rejection) {
        respondError(
            event,
            eventText,
            eventText.length + 1, //+1 since we're expecting stuff after the text
            "Demasiados argumentos para el comando, revisa el comando help para más ayuda."
        )
    }
}