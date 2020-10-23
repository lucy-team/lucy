package command.context

import com.gitlab.kordlib.kordx.commands.model.command.Command
import com.gitlab.kordlib.kordx.commands.model.command.CommandEvent
import com.gitlab.kordlib.kordx.commands.model.processor.CommandProcessor
import discord4j.core.event.domain.message.MessageCreateEvent
import command.LucyEvent
import org.koin.core.Koin

class LucyCommandEvent(
    override val event: MessageCreateEvent,
    override val command: Command<LucyCommandEvent>,
    override val commands: Map<String, Command<*>>,
    private val koin: Koin,
    override val processor: CommandProcessor
) : CommandEvent, LucyEvent {
    override fun getKoin(): Koin = koin

    override val module get() = command.module
}