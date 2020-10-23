package command.argument

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.result.ArgumentResult
import com.gitlab.kordlib.kordx.commands.argument.result.extension.switchOnFail
import command.LucyEvent
import discord4j.core.event.domain.message.MessageCreateEvent

class OptionalArgument<T>(private val argument: Argument<T?, MessageCreateEvent>) : Argument<T?, MessageCreateEvent> by argument {

    override suspend fun parse(text: String, fromIndex: Int, context: MessageCreateEvent): ArgumentResult<T?> {
        return argument.parse(text, fromIndex, context).switchOnFail { ArgumentResult.Success(null, 0) }
    }

}

val OptionalIntArgument = OptionalArgument<Int?>(IntArgument)
