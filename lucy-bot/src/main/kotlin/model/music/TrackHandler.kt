package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import command.context.LucyCommandEvent
import discord4j.core.`object`.entity.Message

class TrackHandler(private val playlist: Playlist, private val event: LucyCommandEvent) : AudioLoadResultHandler {
    override fun loadFailed(exception: FriendlyException?) {
        exception?.let {
            println(it)
        }
    }

    override fun trackLoaded(track: AudioTrack?) {
        track?.let {
            playlist.queue(it)
        }
    }

    override fun noMatches() {
        println("No hay audio")
    }

    override fun playlistLoaded(playlist: AudioPlaylist?) {
        playlist?.let {
            this.playlist.queue(*it.tracks.toTypedArray())
        }
    }

}