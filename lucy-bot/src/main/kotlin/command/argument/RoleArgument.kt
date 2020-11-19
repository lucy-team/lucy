package command.argument

import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.result.ArgumentResult
import com.gitlab.kordlib.kordx.commands.argument.SingleWordArgument
import com.gitlab.kordlib.kordx.commands.argument.result.WordResult
import discord4j.common.util.Snowflake
import discord4j.core.`object`.entity.Role
import discord4j.core.event.domain.message.MessageCreateEvent
import kotlinx.coroutines.reactive.awaitFirst

private val mentionRegex = Regex("""^<@&\d+>$""")

internal class InternalRoleArgument(
    override val name: String = "Role"
) : SingleWordArgument<Role, MessageCreateEvent>() {

    override suspend fun parse(word: String, context: MessageCreateEvent): WordResult<Role> {
        val guildId = context.guildId

        if (!guildId.isPresent)
            return failure("Can't get role outside of guilds.")

        val number = word.toLongOrNull()

        val snowflake = when {
            number != null -> Snowflake.of(number)
            word.matches(mentionRegex) -> Snowflake.of(word.removeSuffix(">").dropWhile { !it.isDigit() })
            else -> return failure("Expected mention.")
        }

        return success(context.guild.awaitFirst().getRoleById(snowflake).awaitFirst())
    }

}

/**
 * Argument that matches a role mention or a role id as a number.
 */
val RoleArgument: Argument<Role, MessageCreateEvent> = InternalRoleArgument()

/**
 * Argument that matches a role mention or a role id as a number.
 *
 * @param name The name of this argument.
 */
@Suppress("FunctionName")
fun RoleArgument(name: String): Argument<Role, MessageCreateEvent> = InternalRoleArgument(name)