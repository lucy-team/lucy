@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package model.music

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState
import java.util.*
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue

class Playlist(val player: AudioPlayer): AudioEventAdapter() {

    enum class RepeatMode {
        NONE,
        PLAYLIST,
        SONG
    }

    private var queue: BlockingDeque<AudioTrack> = LinkedBlockingDeque(200)

    private var current: AudioTrack? = null

    private var repeatMode: RepeatMode = RepeatMode.NONE

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

}