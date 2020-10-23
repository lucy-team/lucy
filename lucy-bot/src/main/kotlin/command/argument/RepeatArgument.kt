package command.argument

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.result.WordResult
import model.music.RepeatMode

internal class InternalRepeatArgument(override val name: String = "Repeat") : SingleWordArgument<RepeatMode, Any?>() {
    override suspend fun parse(word: String, context: Any?): WordResult<RepeatMode> {
        val mode = RepeatMode.values().find { type ->
            type.name.equals(word, true)
        }

        return when (mode) {
            null -> failure("Expected a whole repeat mode.")
            else -> success(mode)
        }
    }

}

val RepeatArgument: Argument<RepeatMode, Any?> = InternalRepeatArgument()
