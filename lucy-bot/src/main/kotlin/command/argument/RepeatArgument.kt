package command.argument

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.result.WordResult
import model.music.Playlist

internal class InternalRepeatArgument(override val name: String = "Repeat") : SingleWordArgument<Playlist.RepeatMode, Any?>() {
    override suspend fun parse(word: String, context: Any?): WordResult<Playlist.RepeatMode> {
        val mode = Playlist.RepeatMode.values().find { type ->
            type.name.equals(word, true)
        }

        return when (mode) {
            null -> failure("Expected a whole repeat mode.")
            else -> success(mode)
        }
    }

}

val RepeatArgument: Argument<Playlist.RepeatMode, Any?> = InternalRepeatArgument()
