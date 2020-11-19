package util

import com.gitlab.kordlib.kordx.commands.model.command.CommandBuilder
import com.gitlab.kordlib.kordx.commands.model.command.CommandEvent
import com.gitlab.kordlib.kordx.commands.model.metadata.MutableMetadata
import com.gitlab.kordlib.kordx.commands.model.module.*
import com.gitlab.kordlib.kordx.commands.model.precondition.Precondition
import com.gitlab.kordlib.kordx.commands.model.processor.BuildEnvironment
import com.gitlab.kordlib.kordx.commands.model.processor.ProcessorContext
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import command.context.LucyCommandEvent
import command.module.LucyModuleBuilder
import discord4j.common.util.Snowflake
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.Message
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.VoiceChannel
import discord4j.core.event.domain.VoiceStateUpdateEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import reactor.core.publisher.Mono
import java.lang.String.format
import java.text.NumberFormat
import java.util.*
import java.util.function.Consumer
import kotlin.math.abs
import com.gitlab.kordlib.kordx.commands.model.precondition.precondition as PreconditionCtr


fun createEmbed(block: EmbedCreateSpec.() -> Unit) = Consumer(block)

fun AudioTrack.embed(message: Message) = createEmbed {
    setTitle("Music")
    setDescription("```css\n${this@embed.info.title}\n```")
    addField("Duration", this@embed.formatTiming(this@embed.duration), true)
    addField("URL", "[Click](${this@embed.info.uri})", true)
    addField("Requested by", message.author.map(User::getMention).get(), true)
    setColor(Color.GREEN)
}

fun AudioTrack.formatTiming(maximum: Long): String {
    var timing = maximum / 1000
    val seconds = timing % 60
    timing /= 60
    val minutes = timing % 60
    timing /= 60
    val hours = timing
    return if (maximum >= 3600000L) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

fun memberCount(event: VoiceStateUpdateEvent, guildId: Snowflake): Mono<Long> {
    return event.client
        .getMemberById(guildId, event.client.selfId)
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .flatMapMany(VoiceChannel::getVoiceStates)
        .flatMap(VoiceState::getMember)
        .filter { !it.isBot }
        .count()
}

fun pluralOf(count: Int, str: String): String? {
    if (str.isBlank()) {
        return null
    }

    return if (abs(count) > 1) {
        format("%s %ss", number(count), str)
    } else format("%s %s", number(count), str)
}

fun number(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.ENGLISH).format(number)
}

fun trackName(info: AudioTrackInfo): String {
    val strBuilder = StringBuilder()

    if (info.title == null) {
        strBuilder.append("Unknown video name")
    } else if ("Unknown artist" == info.author || info.title.startsWith(info.author)) {
        strBuilder.append(info.title.trim { it <= ' ' })
    } else {
        strBuilder.append(String.format("%s - %s", info.author.trim { it <= ' ' }, info.title.trim { it <= ' ' }))
    }

    return strBuilder.toString()
}
