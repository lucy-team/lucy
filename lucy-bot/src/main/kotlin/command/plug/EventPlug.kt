package command.plug

import bot.Lucy
import bot.on
import com.gitlab.kordlib.kordx.commands.model.plug.Plug
import discord4j.core.event.domain.Event

interface EventPlug : Plug {
    fun apply(lucy: Lucy)
}

inline fun <reified T: Event> on(noinline consumer: suspend T.() -> Unit) = object : EventPlug {

    override fun apply(lucy: Lucy) {
        lucy.on<T> { consumer() }
    }
}
