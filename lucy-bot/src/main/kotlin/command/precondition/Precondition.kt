package command.precondition

import command.processor.LucyContext
import command.context.LucyCommandEvent

fun precondition(
    priority: Long = 0,
    filter: suspend LucyCommandEvent.() -> Boolean
) =
    com.gitlab.kordlib.kordx.commands.model.precondition.precondition(LucyContext, priority, filter)