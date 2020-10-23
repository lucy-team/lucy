package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import command.context.LucyCommandEvent
import command.respondEmbed
import discord4j.core.`object`.entity.Message
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.mono
import util.embed
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

enum class RepeatMode {
    NONE,
    PLAYLIST,
    SONG,
    CANCEL
}

interface PlaylistInterface {
    public fun didEmpty()
}

class Playlist(val player: AudioPlayer,
               private val event: LucyCommandEvent): AudioEventAdapter() {

    private var queue: BlockingQueue<AudioTrack>

    private var current: AudioTrack? = null

    private var repeatMode: RepeatMode = RepeatMode.NONE

    var listener: PlaylistInterface? = null

    init {
        queue = LinkedBlockingQueue(200)
    }

    fun queue(vararg tracks: AudioTrack) {
        tracks.forEach {
            if (!player.startTrack(it, true)) {
                queue.offer(it)
            } else {
                current = it
            }
        }
    }

    fun nextTrack(idx: Int) {
        var i = idx - 1
        while (i != 0) {
            val track = queue.poll()

            if (repeatMode == RepeatMode.PLAYLIST)
                queue.offer(track.makeClone())

            i -= 1
        }

        val track = queue.poll()

        if (repeatMode == RepeatMode.PLAYLIST) {
            current = track.makeClone()
            queue.offer(current!!)
        } else {
            current = track
        }

        player.startTrack(current, false)
    }

    fun nextTrack() = nextTrack(1)

    fun shuffle() {
        val new = LinkedBlockingQueue<AudioTrack>(200)
        new.addAll(this.queue.shuffled())
        this.queue = new
    }

    suspend fun showCurrent() {
        current?.let { track ->
            event.channel.awaitSingle().createEmbed(track.embed(event.message)).awaitSingle()
        }
    }

    fun repeat(mode: RepeatMode) {
        repeatMode = mode
    }

    fun clear() = queue.clear()

    fun isEmpty() = queue.isEmpty()

    fun size() = queue.size

    fun iterator() = queue.asIterable().iterator()

    override fun onTrackStart(player: AudioPlayer?, track: AudioTrack?) {
        thread {
            track?.let {
                event.channel.subscribe {
                    it.createEmbed(track.embed(event.message)).subscribe()
                }
            }
        }
    }

    override fun onTrackEnd(player: AudioPlayer?, track: AudioTrack?, endReason: AudioTrackEndReason) {
        if (endReason.mayStartNext) {
            if (repeatMode == RepeatMode.SONG) {
                current = current?.makeClone()
                this.player.startTrack(current, false)
                return
            }

            nextTrack()
        } else {
            listener?.let {
                it.didEmpty()
            }
        }
    }

}