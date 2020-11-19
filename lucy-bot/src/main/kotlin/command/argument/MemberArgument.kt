package command.argument

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument
import com.gitlab.kordlib.kordx.commands.argument.result.WordResult
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Member
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitFirst

private val mentionRegex = Regex("""^<(@|@!)\d+>$""")

internal class InternalMemberArgument(
    override val name: String = "User"
) : SingleWordArgument<Member, MessageCreateEvent>() {

    override suspend fun parse(word: String, context: MessageCreateEvent): WordResult<Member> {
        val guildId = context.guildId

        if (!guildId.isPresent)
            return failure("Can't get members outside of guilds.")

        val number = word.toLongOrNull()
        val snowflake = when {
            number != null -> Snowflake.of(number)
            word.matches(mentionRegex) -> Snowflake.of(word.removeSuffix(">").dropWhile { !it.isDigit() })
            else -> return failure("Expected mention.")
        }

        return success(context.guild.awaitFirst().getMemberById(snowflake).awaitFirst())
    }

}

val MemberArgument: Argument<Member, MessageCreateEvent> = InternalMemberArgument()

@Suppress("FunctionName")
fun MemberArgument(name: String): Argument<Member, MessageCreateEvent> = InternalMemberArgument(name)