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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import model.music.MusicManager
import reactor.core.publisher.Mono
import kotlin.math.ceil


fun musicCommands(music: MusicManager) = module("music") {

    command("play") {

        invoke(StringArgument) { url ->
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(event.member)
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap { channel ->
                    music.join(client, guild.id, channel.id)
                        .flatMap { guildMusic ->
                            guildMusic.play(author.id, url, message.channelId.asLong())
                        }
                }.doOnError {
                    println(it)
                }.awaitSingle()
        }
    }

    command("pause") {
        alias("resume")

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                    .doOnNext { guildMusic ->
                        guildMusic.playlist.player.isPaused = !guildMusic.playlist.player.isPaused
                    }.awaitSingle()
        }
    }

    command("skip") {
        alias("next", "jump")

        invoke(OptionalIntArgument) { idx ->
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                    .doOnNext { guildMusic ->
                        val playlist = guildMusic.playlist
                        if (idx == null)
                            playlist.nextTrack()
                        else
                            playlist.skipTo(idx)
                    }.awaitSingle()
        }
    }


    command("current") {
        invoke {
            if (event.guildId.isPresent) {
                // TODO: Falta este uwu
                //music[event.guildId.get().asString()]?.playlist?.showCurrent()
            }
        }
    }


    command("loop") {
        alias("repeat")

        invoke(RepeatArgument) { mode ->
            event.guildId.ifPresent { guild ->
                //music[guild.asString()]?.playlist?.repeat(mode)
            }
        }
    }

    command("shuffle") {
        alias("random")

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                    .doOnNext { guildMusic ->
                        guildMusic.playlist.shuffle()
                    }.awaitSingle()
        }
    }

    command("stop") {
        alias("leave")

        invoke {
            val guild = guild.awaitSingle()

            music.destroyConnection(guild.id).awaitSingle()
        }
    }

    command("volume") {

        invoke(IntArgument) { volume ->
            val guild = guild.awaitSingle()

            // TODO: Falta este comando uwu
        }
    }

    command("clean") {
        alias("clear")

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                    .doOnNext { guildMusic ->
                        guildMusic.playlist.clear()
                    }.then()
        }
    }

    command("playlist") {
        alias("queue")

        invoke(OptionalIntArgument) { _page ->
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                    .asFlow()
                    .collect { guildMusic ->
                        if (guildMusic.playlist.isEmpty()) {
                            respond("Empty playlist")
                        } else {
                            val itemsPerPage = 10
                            var pages = ceil((guildMusic.playlist.size() / itemsPerPage).toDouble()).toInt()

                            if (pages == 0)
                                pages = 1

                            val page = (_page ?: 1).toString().toInt()

                            val start = (page - 1) * itemsPerPage
                            val end = start + itemsPerPage
                            val playlist = guildMusic.playlist.getPlaylist().toList()

                            var queue = ""

                            for (i in start until end) {
                                queue += "**${i+1}**. [${playlist[i].info.author} - ${playlist[i].info.title}](${playlist[i].info.uri})\n"
                            }

                            respondEmbed {
                                setDescription("**${guildMusic.playlist.size()} tracks:**\n\n${queue}")
                                setFooter("Viewing page ${page}/${pages}", null)
                            }
                        }
                    }
        }
    }
}