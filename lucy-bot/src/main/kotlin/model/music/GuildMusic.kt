package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import command.context.LucyCommandEvent
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import discord4j.voice.VoiceConnection
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration
import java.util.concurrent.atomic.AtomicReference

class GuildMusic(val guildId: Snowflake, manager: AudioPlayerManager, val event: LucyCommandEvent): PlaylistInterface {

    var player: AudioPlayer
        private set

    var playlist: Playlist
        private set

    var provider: LavaPlayerAudioProvider
        private set

    private var leavingTask: AtomicReference<Disposable?> = AtomicReference(null)

    init {
        player = manager.createPlayer()
        playlist = Playlist(player, event)
        player.addListener(playlist)
        playlist.listener = this
        provider = LavaPlayerAudioProvider(player)
    }

    fun handleLeave() {
        leavingTask.set(Mono.delay(Duration.ofSeconds(10), Schedulers.boundedElastic())
            .filter { isLeavingScheduled() }
            .map { event.client.voiceConnectionRegistry }
            .flatMap { it.getVoiceConnection(guildId) }
            .flatMap(VoiceConnection::disconnect)
            .subscribe())
    }

    fun isLeavingScheduled(): Boolean {
        return leavingTask.get() != null && !leavingTask.get()!!.isDisposed
    }

    fun cancelLeave() {
        if (isLeavingScheduled()) {
            leavingTask.get()!!.dispose()
        }
    }

    fun destroy() {
        cancelLeave()
        player.destroy()
        playlist.listener = null
    }

    override fun didEmpty() {
        handleLeave()
    }

}