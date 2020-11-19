package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER
import discord4j.common.util.Snowflake
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers


class AudioLoadResultListener(
    private val guildId: Snowflake,
    private val djId: Snowflake,
    private val url: String,
    private val insertFirst: Boolean,
    private val musicManager: MusicManager
): AudioLoadResultHandler {

    //private var resultTracks: List<AudioTrack> = emptyList()

    override fun trackLoaded(track: AudioTrack?) {
        Mono.justOrEmpty(musicManager.getGuildMusic(guildId))
            .filter { guildMusic -> !guildMusic.playlist.queue(track!!, insertFirst) }
            .flatMap(GuildMusic::channel)
            .flatMap { channel ->
                channel.createMessage("Musica agregada")
            }
            .then(terminate())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        when {
            playlist!!.tracks.isEmpty() -> {
                this.onNoMatches();
            }
            // If a track is specifically selected
            playlist.selectedTrack != null -> {
                this.trackLoaded(playlist.selectedTrack);
            }
            // The user loads a full playlist
            else -> {
                this.onPlaylistLoaded(playlist);
            }
        }
    }

    override fun noMatches() {
        onNoMatches()
    }

    override fun loadFailed(exception: FriendlyException?) {
        Mono.justOrEmpty(musicManager.getGuildMusic(guildId))
            .flatMap { guildMusic ->
                guildMusic.channel.flatMap { it.createMessage("No se pudo cargar la musica $exception") }
            }
            .then(terminate())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun onPlaylistLoaded(playlist: AudioPlaylist) = Mono.justOrEmpty(musicManager.getGuildMusic(guildId))
        .flatMap { guildMusic ->
            val trackScheduler = guildMusic.playlist

            for (track in playlist.tracks) {
                trackScheduler.queue(track, insertFirst)
            }

            guildMusic.channel
                .flatMap { channel -> channel.createMessage("Se agrego la playlist") }
        }
        .then(terminate())
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe()

    private fun terminate() = Mono.justOrEmpty(musicManager.getGuildMusic(guildId))
        .flatMap { guildMusic -> guildMusic.removeAudioLoadResultListener(this) }

    private fun onNoMatches() = Mono.justOrEmpty(musicManager.getGuildMusic(guildId))
        .flatMap(GuildMusic::channel)
        .flatMap { channel ->
            channel.createMessage("No hubo resultados")
        }
        .then(terminate())
        .subscribeOn(Schedulers.boundedElastic())
        .subscribe()

}