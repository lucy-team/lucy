package command

import com.gitlab.kordlib.kordx.commands.model.command.CommandBuilder
import discord4j.core.event.domain.message.MessageCreateEvent
import command.context.LucyCommandEvent

typealias LucyCommandBuilder = CommandBuilder<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent>
