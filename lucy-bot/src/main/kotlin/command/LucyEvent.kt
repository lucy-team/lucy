package command

import discord4j.core.`object`.entity.Message
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.core.spec.MessageCreateSpec
import kotlinx.coroutines.reactive.awaitSingle

interface LucyEvent {

    val event: MessageCreateEvent

    val client get() = event.client

    val message get() = event.message

    val author get() = message.author.get()

    val channel get() = message.channel

    val guild get() = message.guild

    suspend fun respond(message: String): Message {
        return channel.awaitSingle().createMessage(message).awaitSingle()
    }

    companion object {
        operator fun invoke(event: MessageCreateEvent) = object : LucyEvent {
            override val event: MessageCreateEvent = event
        }
    }

}

suspend inline fun LucyEvent.respond(crossinline builder: MessageCreateSpec.() -> Unit): Message {
    return channel.awaitSingle().createMessage { it.apply(builder) }.awaitSingle()
}

suspend inline fun LucyEvent.respondEmbed(crossinline builder: EmbedCreateSpec.() -> Unit): Message {
    return channel.awaitSingle().createEmbed { it.apply(builder) }.awaitSingle()
}