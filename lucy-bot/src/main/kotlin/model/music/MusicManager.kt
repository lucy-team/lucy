package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers
import com.sedmelluq.discord.lavaplayer.track.playback.AllocatingAudioFrameBuffer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrameBufferFactory
import command.LucyEvent
import command.context.LucyCommandEvent
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Message
import java.util.concurrent.ConcurrentHashMap

class MusicManager {

    private val managers: ConcurrentHashMap<String, GuildMusic> = ConcurrentHashMap()

    private val playerManager: AudioPlayerManager

    init {
        playerManager = DefaultAudioPlayerManager()
        playerManager.configuration.frameBufferFactory = AudioFrameBufferFactory(::AllocatingAudioFrameBuffer)
        AudioSourceManagers.registerRemoteSources(playerManager)
    }

    operator fun get(guild: String): GuildMusic? {
        return managers[guild]
    }

    fun put(guild: String, event: LucyCommandEvent) {
        if (!managers.containsKey(guild)) {
            managers[guild] = GuildMusic(Snowflake.of(guild), playerManager, event)
        }
    }

    fun play(guild: String, url: String, event: LucyCommandEvent) {
        if (!managers.containsKey(guild)) {
            put(guild, event)
        }

        get(guild)?.let {
            playerManager.loadItem(url, TrackHandler(it.playlist, event))
        }
    }

    fun remove(guild: String) {
        val musicGuild: GuildMusic? = managers.remove(guild)
        musicGuild?.destroy()
    }

}
