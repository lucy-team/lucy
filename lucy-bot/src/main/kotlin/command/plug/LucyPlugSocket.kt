package command.plug

import bot.Lucy
import com.gitlab.kordlib.kordx.commands.model.plug.PlugContainer
import com.gitlab.kordlib.kordx.commands.model.plug.PlugSocket

class LucyPlugSocket(private val lucy: Lucy) : PlugSocket {

    override suspend fun handle(container: PlugContainer) {
        container.getPlugs<EventPlug>().forEach { it.apply(lucy) }
    }

}