package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.AllocatingAudioFrameBuffer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import discord4j.common.util.Snowflake
import discord4j.core.GatewayDiscordClient
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.voice.AudioProvider
import discord4j.voice.VoiceConnection
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicBoolean


class MusicManager {

    private val guildMusics: ConcurrentHashMap<Snowflake, GuildMusic> = ConcurrentHashMap()

    private val guildJoining: ConcurrentHashMap<Snowflake, AtomicBoolean> = ConcurrentHashMap()

    private val playerManager: AudioPlayerManager

    init {
        playerManager = DefaultAudioPlayerManager()
        playerManager.configuration.apply {
            frameBufferFactory = AudioFrameBufferFactory(::AllocatingAudioFrameBuffer)
            isFilterHotSwapEnabled = true
        }

        AudioSourceManagers.registerRemoteSources(playerManager)
    }

    fun join(client: GatewayDiscordClient, guildId: Snowflake, voiceChannelId: Snowflake): Mono<GuildMusic> {
        return Mono.justOrEmpty(getGuildMusic(guildId))
            .switchIfEmpty(Mono.defer {
                val audioPlayer = playerManager.createPlayer()
                audioPlayer.addListener(TrackEventListener(guildId, this))
                val audioProvider = LavaPlayerAudioProvider(audioPlayer)

                this.join(client, guildId, voiceChannelId, audioProvider)
                    .map { Playlist(audioPlayer) }
                    .map { trackScheduler -> GuildMusic(client, guildId, trackScheduler, this) }
                    .doOnNext { guildMusic ->
                        this.guildMusics[guildId] = guildMusic
                    }
            })
    }

    private fun join(client: GatewayDiscordClient, guildId: Snowflake, voiceChannelId: Snowflake, audioProvider: AudioProvider): Mono<VoiceConnection> {
        if (this.guildJoining.computeIfAbsent(guildId) { AtomicBoolean() }.getAndSet(true)) {
            return Mono.empty()
        }

        val isDisconnected = client.voiceConnectionRegistry
            .getVoiceConnection(guildId)
            .flatMapMany(VoiceConnection::stateEvents)
            .next()
            .map(VoiceConnection.State.DISCONNECTED::equals)
            .defaultIfEmpty(true)

        return client.getChannelById(voiceChannelId)
            .cast(VoiceChannel::class.java) // Do not join the voice channel if the current voice connection is in not disconnected
            .filterWhen { isDisconnected }
            .flatMap { voiceChannel ->
                voiceChannel.join { spec ->
                    spec.setSelfDeaf(true)
                    spec.setProvider(audioProvider)
                }
            }
            .doOnTerminate { this.guildJoining.remove(guildId) }
    }

    fun getGuildMusic(guildId: Snowflake): Optional<GuildMusic> {
        val guildMusic: GuildMusic? = this.guildMusics[guildId]
        return Optional.ofNullable(guildMusic)
    }

    fun loadItemOrdered(
        guildId: Long,
        identifier: String,
        listener: AudioLoadResultHandler
    ): Future<Void> {
        return this.playerManager.loadItemOrdered(guildId, identifier, listener)
    }

    fun destroyConnection(guildId: Snowflake): Mono<Void> {
        val guildMusic = guildMusics.remove(guildId)

        guildMusic?.destroy()

        return Mono.justOrEmpty(guildMusic)
            .map(GuildMusic::client)
            .map(GatewayDiscordClient::getVoiceConnectionRegistry)
            .flatMap { registry -> registry.getVoiceConnection(guildId) }
            .flatMap { vc -> vc.disconnect() }
    }

}
