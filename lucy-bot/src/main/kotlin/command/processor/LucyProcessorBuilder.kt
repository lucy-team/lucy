package command.processor

import bot.Lucy
import com.gitlab.kordlib.kordx.commands.model.plug.PlugContainer
import com.gitlab.kordlib.kordx.commands.model.processor.*

class LucyProcessorBuilder(val lucy: Lucy) : ProcessorBuilder() {

    override suspend fun build(): CommandProcessor {
        return super.build()
    }

}