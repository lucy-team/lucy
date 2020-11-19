@file:Suppress("EXPERIMENTAL_API_USAGE")

package bot

import discord4j.core.DiscordClient
import discord4j.core.event.domain.Event
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.mono
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.channels.Channel as CoroutineChannel

class Lucy(private val token: String): CoroutineScope {

    private val eventPublisher: BroadcastChannel<Event> = BroadcastChannel(1)

    val events get() = eventPublisher.asFlow().buffer(CoroutineChannel.UNLIMITED)

    override val coroutineContext: CoroutineContext
        get() = dispatcher + Job()

    private val dispatcher: CoroutineDispatcher = Dispatchers.Default

    suspend fun build() {
        val client = DiscordClient.create(token)

        client.withGateway {
            mono(CoroutineName("GatewayCoroutine")) {
                launch {
                    it.on(MessageCreateEvent::class.java)
                        .asFlow()
                        .collect(eventPublisher::send)
                }

                launch {
                    it.on(VoiceStateUpdateEvent::class.java)
                        .asFlow()
                        .collect(eventPublisher::send)
                }
            }
        }.awaitLast()
    }
}

inline fun <reified T: Event> Lucy.on(scope: CoroutineScope = this, noinline consumer: suspend T.() -> Unit): Job =
    events.buffer(CoroutineChannel.UNLIMITED)
        .filterIsInstance<T>()
        .onEach {
            scope.launch { runCatching { consumer(it) }.onFailure { print(it) } }
        }
        .launchIn(scope)
