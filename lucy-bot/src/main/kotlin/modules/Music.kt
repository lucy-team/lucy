@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.argument.OptionalIntArgument
import command.argument.RepeatArgument
import command.module.module
import command.respondEmbed
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Member
import discord4j.voice.VoiceConnection
import kotlinx.coroutines.reactive.awaitSingle
import model.music.MusicManager
import reactor.core.publisher.Mono
import kotlin.math.ceil

fun musicCommands(music: MusicManager) = module("music") {

    command("play") {

        invoke(StringArgument) { url ->
            val guild = event.guildId.get().asString()

            music.put(guild, this)

            Mono.justOrEmpty(music[guild])
                .flatMap {
                    Mono.justOrEmpty(event.member)
                        .flatMap(Member::getVoiceState)
                        .flatMap(VoiceState::getChannel)
                        .flatMap { channel ->
                            channel.join { spec ->
                                spec.setSelfDeaf(true)
                                spec.setProvider(it.provider)
                            }
                        }
                }.awaitSingle()

            music.play(guild, url, this)
        }
    }

    command("pause") {
        alias("resume")

        invoke {
            event.guildId.ifPresent {
                music[it.asString()]?.let { guildMusic ->
                    guildMusic.player.isPaused = !guildMusic.player.isPaused
                }
            }
        }
    }

    command("skip") {
        alias("next", "jump")

        invoke(OptionalIntArgument) { idx ->
            event.guildId.ifPresent { id ->
                music[id.asString()]?.playlist?.nextTrack(idx ?: 1)
            }
        }
    }


    command("current") {
        invoke {
            if (event.guildId.isPresent) {
                music[event.guildId.get().asString()]?.playlist?.showCurrent()
            }
        }
    }


    command("loop") {
        alias("repeat")

        invoke(RepeatArgument) { mode ->
            event.guildId.ifPresent { guild ->
                music[guild.asString()]?.playlist?.repeat(mode)
            }
        }
    }

    command("shuffle") {
        alias("random")

        invoke {
            event.guildId.ifPresent { id ->
                music[id.asString()]?.playlist?.shuffle()
            }
        }
    }

    command("stop") {
        alias("leave")

        invoke {
            val guild = event.guildId.get()

            music[guild.asString()]?.let {
                Mono.just(event.message.client)
                    .map { it.voiceConnectionRegistry }
                    .flatMap { it.getVoiceConnection(guild) }
                    .flatMap(VoiceConnection::disconnect)
                    .subscribe()

                music.remove(guild.asString())
            }
        }
    }

    command("volume") {

        invoke(IntArgument) { volume ->
            val guild = event.guildId.get().asString()

            // TODO: Falta este comando uwu
        }
    }

    command("clean") {
        alias("clear")

        invoke {
            val guild = event.guildId.get().asString()

            music[guild]?.let {
                it.playlist.clear()
            }
        }
    }

    command("playlist") {
        alias("queue")

        invoke(OptionalIntArgument) { _page ->
            val guild = event.guildId.get().asString()

            music[guild]?.let {
                if (it.playlist.isEmpty()) {
                    respond("Empty playlist")
                    return@invoke
                }

                val itemsPerPage = 10
                var pages = ceil((it.playlist.size() / itemsPerPage).toDouble()).toInt()

                if (pages == 0)
                    pages = 1

                val page = (_page ?: 1).toString().toInt()

                var start = (page - 1) * itemsPerPage
                val end = start + itemsPerPage
                val iterator = it.playlist.iterator()

                var i = 0

                while (i < start) {
                    iterator.next()
                    i += 1
                }

                var queue = ""
                while (start < end && iterator.hasNext()) {
                    val data = iterator.next()
                    queue += "`${start + 1}.` [**${data.info.title}**](${data.info.uri})\n"
                    start += 1
                }

                respondEmbed {
                    setDescription("**${it.playlist.size()} tracks:**\n\n${queue}")
                    setFooter("Viewing page ${page}/${pages}", null)
                }
            }
        }
    }
}