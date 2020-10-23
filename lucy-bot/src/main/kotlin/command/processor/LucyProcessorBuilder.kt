package command.processor

import bot.Lucy
import com.gitlab.kordlib.kordx.commands.model.processor.CommandProcessor
import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorBuilder

class LucyProcessorBuilder(val lucy: Lucy) : ProcessorBuilder() {

    override suspend fun build(): CommandProcessor {
        return super.build()
    }

}