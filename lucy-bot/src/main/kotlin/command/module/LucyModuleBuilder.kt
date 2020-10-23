package command.module

import com.gitlab.kordlib.kordx.commands.model.module.ModuleBuilder
import com.gitlab.kordlib.kordx.commands.model.module.ModuleModifier
import discord4j.core.event.domain.message.MessageCreateEvent
import com.gitlab.kordlib.kordx.commands.model.module.module
import command.context.LucyCommandEvent
import command.processor.LucyContext

typealias LucyModuleBuilder = ModuleBuilder<MessageCreateEvent, MessageCreateEvent, LucyCommandEvent>

inline fun module(
    name: String,
    crossinline builder: suspend LucyModuleBuilder.() -> Unit
) : ModuleModifier = module(name, LucyContext) {
    builder()
}