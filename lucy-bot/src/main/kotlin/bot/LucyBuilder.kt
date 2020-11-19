@file:Suppress("EXPERIMENTAL_API_USAGE")

package bot

import com.gitlab.kordlib.kordx.commands.model.context.CommonContext
import com.gitlab.kordlib.kordx.commands.model.processor.BaseEventHandler
import com.gitlab.kordlib.kordx.commands.model.processor.CommandProcessor
import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorBuilder
import command.plug.LucyPlugSocket
import command.processor.*
import org.koin.dsl.module

class BotBuilder(val lucy: Lucy, val processorBuilder: LucyProcessorBuilder = LucyProcessorBuilder(lucy)) {

    //val ignoreSelf = eventFilter { message.author.id != lucy.selfId }

    private val ignoreBots = eventFilter { !message.author.get().isBot }

    init {
        processor {
            +ignoreBots
            //+ignoreSelf
        }


        processor {
            koin {
                modules(module { single { lucy } })
            }
            +LucyPlugSocket(lucy)
        }
    }

    inline fun processor(builder: ProcessorBuilder.() -> Unit) {
        processorBuilder.apply(builder)
    }

    suspend fun build(): CommandProcessor = processorBuilder.apply {
        eventSources += LucyEventSource(lucy)

        if (eventHandlers[LucyContext] == null && eventHandlers[CommonContext] == null) {
            eventHandlers[LucyContext] = BaseEventHandler(LucyContext, LucyContextConverter, LucyErrorHandler())
        }

        if (prefixBuilder[LucyContext] == null && prefixBuilder[CommonContext] == null) {
            print("Definir prefijo")
        }
    }.build()
}

suspend inline fun bot(token: String, configure: LucyProcessorBuilder.() -> Unit) = bot(Lucy(token), configure)

suspend inline fun bot(lucy: Lucy, configure: LucyProcessorBuilder.() -> Unit) {
    val builder = BotBuilder(lucy)
    builder.processorBuilder.apply(configure)
    builder.build()
    lucy.build()
}
