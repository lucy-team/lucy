@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import util.pluralOf
import util.trackName
import java.lang.String.format
import java.util.*
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.ceil
import kotlin.time.ExperimentalTime

class Playlist(val player: AudioPlayer) : AudioEventAdapter() {

    enum class RepeatMode {
        NONE,
        PLAYLIST,
        SONG
    }

    private var queue: BlockingDeque<AudioTrack> = LinkedBlockingDeque(200)

    private var current: AudioTrack? = null

    var repeatMode: RepeatMode = RepeatMode.NONE

    fun queue(track: AudioTrack, first: Boolean): Boolean {
        return when {
            player.startTrack(track.makeClone(), true) -> {
                this.current = track;
                true
            }
            first -> {
                this.queue.offerFirst(track);
                false
            }
            else -> {
                this.queue.offerLast(track);
                false
            }
        }
    }

    fun nextTrack(): Boolean {
        when (repeatMode) {
            RepeatMode.PLAYLIST -> {
                queue.offer(this.current)
            }
            RepeatMode.NONE -> {
                this.current = queue.poll()
                return this.player.startTrack(current?.makeClone(), false)
            }
            RepeatMode.SONG -> player.playTrack(current?.makeClone())
        }

        return true
    }

    fun skipTo(num: Int) {
        var track: AudioTrack? = null
        for (i in 0 until num) {
            track = queue.poll()
        }
        this.player.playTrack(track?.makeClone())
        this.current = track
    }

    fun shuffle() {
        val new = LinkedBlockingQueue(queue)
        this.queue.clear()
        this.queue.addAll(new.shuffled())
    }

    fun repeat(mode: RepeatMode) {
        repeatMode = mode
    }

    fun clear() = queue.clear()

    fun isEmpty() = queue.isEmpty()

    fun size() = queue.size

    fun getPlaylist(): Collection<AudioTrack> {
        return Collections.unmodifiableCollection(queue)
    }

    fun destroy() {
        if (current != null && current!!.state === AudioTrackState.PLAYING) {
            current!!.stop()
        }

        player.destroy()
        clear()
    }

    fun isPlaying(): Boolean {
        return this.player.playingTrack != null
    }

    fun isStopped(): Boolean {
        return queue.isEmpty() && !isPlaying()
    }

    fun setVolume(volume: Int) {
        this.player.volume = volume
    }

    fun getCurrent(): AudioTrack? {
        return current
    }

    fun asString(_page: Int?): String {
        if (queue.isEmpty()) {
            return "**La playlist esta vacia.**"
        }

        if (_page != null) {
            if (_page <= 0)
                return "**Solo se permiten paginas mayores a 0.**"
        }

        val itemsPerPage = 10
        var pages = ceil((queue.size / itemsPerPage).toDouble()).toInt()

        if (pages == 0)
            pages = 1

        var page = (_page ?: 1)

        if (page > pages)
            page = pages

        val playlistStr = StringBuilder("**Playlist**\n")

        val start = (page - 1) * itemsPerPage
        var end = start + itemsPerPage

        if (end >= queue.size)
            end = queue.size

        val playlist = getPlaylist().toList()

        for (i in start until end) {
            val name = format("%n\t**%d.** [%s](%s)",
                i + 1, trackName(playlist[i].info), playlist[i].info.uri)
            playlistStr.append(name)
        }

        playlistStr.append("\nPaginas ${page}/${pages}")

        return playlistStr.toString()
    }

}