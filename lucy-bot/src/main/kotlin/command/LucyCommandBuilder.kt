package command

import com.gitlab.kordlib.kordx.commands.model.command.CommandBuilder
import com.gitlab.kordlib.kordx.commands.model.metadata.Metadata
import discord4j.core.event.domain.message.MessageCreateEvent
import command.context.LucyCommandEvent

object HelpKey : Metadata.Key<String>

typealias LucyCommandBuilder = CommandBuilder<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent>
