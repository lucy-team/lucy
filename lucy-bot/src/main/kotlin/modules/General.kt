@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.HelpKey
import command.module.module
import command.respondEmbed
import discord4j.core.spec.EmbedCreateSpec
import java.util.function.Consumer

fun generalCommands() = module("general") {
    command("help") {
        metaData[HelpKey] = "Este mensaje."

        invoke {
            var count = 1
            val embed = Consumer<EmbedCreateSpec> { embed ->
                embed.setTitle("**Lista de comandos:**\n")
                modules.map { module ->
                    val commands = module.value.commands.map {
                        "\t **${it.key}**: ${it.value.data.metadata[HelpKey] ?: ""}"
                    }

                    embed.addField("$count. **${module.key}**\n", commands.joinToString("\n"), false)

                    count += 1
                }
            }

            respondEmbed(embed)
        }
    }
}
