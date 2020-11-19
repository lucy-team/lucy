@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.HelpKey
import command.module.module
import discord4j.rest.util.Color
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

fun funCommands(client: HttpClient) = module("fun") {

    command("cat") {
        metaData[HelpKey] = "Muestra la foto de un gatito owo."

        invoke {
            val msg = respond("Buscando gatito owo...")

            val content: HttpResponse = client.get("http://thecatapi.com/api/images/get?format=src&results_per_page=1")

            msg.edit {
                it.setContent("")
                it.setEmbed { embed ->
                    embed.setTitle("\uD83D\uDC31 Meowww..")
                    embed.setColor(Color.of(0x3498db))
                    embed.setImage(content.request.url.toString())
                    embed.setUrl(content.request.url.toString())
                }
            }.awaitSingle()
        }
    }

    command("dog") {
        metaData[HelpKey] = "Muestra la foto de un perrito owo."

        invoke {
            val msg = respond("Buscando perrito owo...")

            val content: JsonObject = client.get("https://dog.ceo/api/breeds/image/random")

            msg.edit {
                it.setContent("")
                it.setEmbed { embed ->
                    embed.setTitle("\uD83D\uDC36 Woof!")
                    embed.setColor(Color.of(0x3498db))
                    embed.setImage(content["message"]!!.jsonPrimitive.content)
                    embed.setUrl(content["message"]!!.jsonPrimitive.content)
                }
            }.awaitSingle()
        }
    }
}