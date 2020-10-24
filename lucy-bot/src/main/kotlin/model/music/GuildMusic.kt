package model.music

import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.MessageChannel
import discord4j.voice.VoiceConnection
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference


class GuildMusic(
    val client: GatewayDiscordClient,
    private val guildId: Snowflake,
    val playlist: Playlist,
    private val musicManager: MusicManager
) {

    private val LEAVE_DELAY = Duration.ofMinutes(1)

    private val listeners: ConcurrentHashMap<AudioLoadResultListener, Future<Void>> = ConcurrentHashMap()
    var isWaitingForChoice = AtomicBoolean(false)
    var messageChannelId = AtomicLong()
    var djId = AtomicLong()
    private var leavingTask: AtomicReference<Disposable?> = AtomicReference(null)

    fun handleLeave() {
        leavingTask.set(Mono.delay(LEAVE_DELAY, Schedulers.boundedElastic())
            .filter { isLeavingScheduled() }
            .map { client.voiceConnectionRegistry }
            .flatMap { it.getVoiceConnection(guildId) }
            .flatMap(VoiceConnection::disconnect)
            .subscribe())
    }

    fun isLeavingScheduled(): Boolean {
        return leavingTask.get() != null && !leavingTask.get()!!.isDisposed
    }

    fun addAudioLoadResultListener(listener: AudioLoadResultListener, url: String) {
        listeners[listener] = musicManager.loadItemOrdered(guildId.asLong(), url, listener)
    }

    fun removeAudioLoadResultListener(listener: AudioLoadResultListener): Mono<Void> {
        listeners.remove(listener)
        // If there is no music playing and nothing is loading, leave the voice channel
        return if (playlist.isStopped() && listeners.values.stream().allMatch { it.isDone }) {
            client.voiceConnectionRegistry
                .getVoiceConnection(guildId)
                .flatMap { vc -> vc.disconnect() }
        } else Mono.empty()
    }

    fun end(): Mono<Void> {
        return client.voiceConnectionRegistry
            .getVoiceConnection(guildId)
            .flatMap { vc -> vc.disconnect() }
            .then(channel)
            .flatMap { channel -> channel.createMessage("Se acabo la playlist uwu.") }
            .then()
    }

    fun play(authorId: Snowflake, url: String, channelId: Long): Mono<Void> {
        if (isWaitingForChoice.get()) {
            if (djId.get() == authorId.asLong()) {
                return Mono.error(Exception("Se esta seleccionado"))
            }
        }

        return Mono.create {
            val resultListener = AudioLoadResultListener(guildId, authorId, url, false, musicManager)

            messageChannelId.set(channelId)
            addAudioLoadResultListener(resultListener, url)
        }
    }

    fun cancelLeave() {
        if (isLeavingScheduled()) {
            leavingTask.get()!!.dispose()
        }
    }

    fun destroy() {
        cancelLeave()
        listeners.values.forEach { it.cancel(true) }
        listeners.clear()
        playlist.destroy()
    }

    val channel get() = this.client.getChannelById(Snowflake.of(messageChannelId.get())).cast(MessageChannel::class.java)

}