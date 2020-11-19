package command.module

import com.gitlab.kordlib.kordx.commands.model.module.CommandSet
import command.LucyCommandBuilder
import command.processor.LucyContext

fun commands(
    builder: LucyModuleBuilder.() -> Unit
) = com.gitlab.kordlib.kordx.commands.model.module.commands(LucyContext, builder)

/**
 * Defines a [CommandSet] with a single command with the given [name] and configured by the [builder].
 */
fun command(
    name: String,
    builder: LucyCommandBuilder.() -> Unit
): CommandSet = com.gitlab.kordlib.kordx.commands.model.module.command(LucyContext, name, builder)