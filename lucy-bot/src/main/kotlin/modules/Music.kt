@file:AutoWired

package modules

import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import command.HelpKey
import command.argument.OptionalIntArgument
import command.argument.RepeatArgument
import command.module.module
import command.respondEmbed
import discord4j.core.`object`.VoiceState
import discord4j.core.`object`.entity.Member
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.collect
import model.music.MusicManager
import reactor.core.publisher.Mono
import util.trackName
import java.lang.String.format

fun musicCommands(music: MusicManager) = module("music") {

    command("play") {
        metaData[HelpKey] = "[url] Se conecta al canal de voz y reproduce la musica."

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
                }.doOnError { error ->
                    message.channel
                        .flatMap {
                            it.createMessage(error.message ?: "Upps hubo un error.")
                        }
                }.awaitSingle()
        }
    }

    command("pause") {
        alias("resume")

        metaData[HelpKey] = "Pausa o continua la repoducción de la musica."

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect { guildMusic ->
                    guildMusic.playlist.player.isPaused = !guildMusic.playlist.player.isPaused
                }
        }
    }

    command("skip") {
        alias("next", "jump")

        metaData[HelpKey] = "[numero (opcional)] Avanza a la siguiente musica o al numero que se le indica."

        invoke(OptionalIntArgument) { idx ->
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect { guildMusic ->
                    val playlist = guildMusic.playlist
                    if (idx == null)
                        playlist.nextTrack()
                    else
                        playlist.skipTo(idx)
                }
        }
    }


    command("current") {
        metaData[HelpKey] = "Muestra el titulo de la musica en reproducción actual."

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .map { guildMusic ->
                    val track = guildMusic.playlist.getCurrent()
                    if (track == null)
                        return@map "No hay musica en reproducción."
                    else
                        return@map format("Musica reproducción: **%s**", trackName(track.info))
                }.collect {
                    respondEmbed {
                        setDescription(it)
                    }
                }

        }
    }


    command("loop") {
        alias("repeat")

        metaData[HelpKey] = "[modo (SONG, NONE)] Pone en repetición una musica."

        invoke(RepeatArgument) { mode ->
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect {
                    it.playlist.repeatMode = mode

                    respond("Se cambio el modo de repeticion a $mode")
                }
        }
    }

    command("shuffle") {
        alias("random")

        metaData[HelpKey] = "Reordena de manera aleatoria la playlist."

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect { guildMusic ->
                    guildMusic.playlist.shuffle()
                    respond("La playlist se ha ordenado de forma aleatoria")
                }
        }
    }

    command("stop") {
        alias("leave", "exit")

        metaData[HelpKey] = "El bot termina la reproducción de la musica y sale del canal de voz."

        invoke {
            val guild = guild.awaitSingle()

            music.destroyConnection(guild.id).awaitSingle()
        }
    }

    command("volume") {

        metaData[HelpKey] = "[numero] Asigna el volumen al reproductor."

        invoke(IntArgument) { volume ->
            if (volume < 1 || volume > 100) {
                respond("El volumen debe ser mayor a 1 y menor que 100.")
                return@invoke
            }

            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect { guildMusic ->
                    guildMusic.playlist.setVolume(volume)
                    respond("Volumen: $volume")
                }
        }
    }

    command("clean") {
        alias("clear")

        metaData[HelpKey] = "Elimina la playlist."

        invoke {
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect { guildMusic ->
                    guildMusic.playlist.clear()
                    respond("La playlist se ha borrado")
                }
        }
    }

    command("playlist") {
        alias("queue")

        metaData[HelpKey] = "[numero (pagina opcional)] Muestra la cola actual del reproductor."

        invoke(OptionalIntArgument) { _page ->
            val guild = guild.awaitSingle()

            Mono.justOrEmpty(music.getGuildMusic(guild.id))
                .collect { guildMusic ->
                    respondEmbed {
                        setDescription(guildMusic.playlist.asString(_page))
                    }
                }
        }
    }
}