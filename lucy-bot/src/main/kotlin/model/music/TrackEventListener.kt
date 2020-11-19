package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import discord4j.common.util.Snowflake
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.atomic.AtomicInteger


class TrackEventListener(private val guildId: Snowflake, private val manager: MusicManager): AudioEventAdapter() {

    private val errorCount = AtomicInteger()

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack) {
        Mono.justOrEmpty(manager.getGuildMusic(guildId))
            .flatMap { guildMusic ->
                guildMusic.channel
                    .flatMap { channel -> channel.createMessage(track.info.title) }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
        Mono.justOrEmpty(manager.getGuildMusic(guildId))
            .filter { endReason == AudioTrackEndReason.FINISHED } // Everything seems fine, reset error counter.
            .doOnNext { errorCount.set(0) }
            .flatMap { this.nextOrEnd() }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    override fun onTrackException(player: AudioPlayer?, track: AudioTrack?, exception: FriendlyException?) {
        Mono.justOrEmpty(manager.getGuildMusic(guildId))
            .flatMap { guildMusic ->
                guildMusic.channel
                    .flatMap { channel -> channel.createMessage("El meo error xd $exception") }
                    .then(nextOrEnd())
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    override fun onTrackStuck(player: AudioPlayer?, track: AudioTrack?, thresholdMs: Long) {
        Mono.justOrEmpty(manager.getGuildMusic(guildId))
            .flatMap(GuildMusic::channel)
            .flatMap { channel ->
                channel.createMessage("Se quedo pegada la musica xd")
            }
            .then(nextOrEnd())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }


    private fun nextOrEnd() = Mono.justOrEmpty(manager.getGuildMusic(guildId)) // If the next track could not be started
        .filter { guildMusic -> !guildMusic.playlist.nextTrack() }
        .flatMap { it.end() }
}