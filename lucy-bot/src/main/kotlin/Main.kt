@file:AutoWired

import bot.bot
import bot.on
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import com.gitlab.kordlib.kordx.commands.model.prefix.prefix
import command.HelpKey
import command.module.commands
import command.precondition.precondition
import discord4j.core.event.domain.VoiceStateUpdateEvent
import kapt.kotlin.generated.configure
import model.music.MusicManager
import command.prefix.db
import command.prefix.lucy
import discord4j.core.`object`.entity.channel.GuildChannel
import discord4j.rest.util.Permission
import org.koin.core.inject
import reactor.core.publisher.Mono
import util.memberCount
import io.github.cdimascio.dotenv.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.util.*
import kotlinx.coroutines.reactive.awaitFirst

val dotenv = dotenv {
    filename = ".env"
    ignoreIfMissing = true
    systemProperties = true
}

suspend fun main() = bot(dotenv["BOT_TOKEN"]) {
    configure()

    lucy.on<VoiceStateUpdateEvent> {
        val music: MusicManager by inject()
        val userId = current.userId
        val guildId = current.guildId

        if (userId == client.selfId) {
            if (!current.channelId.isPresent && old.isPresent) {
                music.destroyConnection(guildId)
            }
        } else {
            Mono.defer {
                Mono.justOrEmpty(music.getGuildMusic(guildId))
                    .flatMap { guildMusic ->
                        memberCount(this, guildId)
                            .filter { memberCount -> (memberCount == 0L) != guildMusic.isLeavingScheduled() }
                            .map { memberCount ->
                                println("1")
                                println("count: ${memberCount}")
                                if (memberCount == 0L && !guildMusic.isLeavingScheduled()) {
                                    println("1.1")
                                    guildMusic.playlist.player.isPaused = true
                                    guildMusic.handleLeave()
                                } else if (memberCount != 0L && guildMusic.isLeavingScheduled()) {
                                    println("1.2")
                                    guildMusic.playlist.player.isPaused = false
                                    guildMusic.cancelLeave()
                                }
                            }
                    }.then()
            }.subscribe()
        }
    }

}

val prefix = prefix {
    lucy {
        db()
    }
}

val dependencies = org.koin.dsl.module {
    single { MusicManager() }
    single {
        HttpClient(CIO) {
            install(JsonFeature) {
                serializer = KotlinxSerializer()
            }
        }
    }
}

val precondition = precondition {
    if (command.module.name in listOf("moderator", "rol")) {
        val permission =
            member.basePermissions.map { Permission.ADMINISTRATOR in it }
                .awaitFirst()
        val botPermission =
            guild.awaitFirst().getChannelById(channel.awaitFirst().id).cast(GuildChannel::class.java).flatMap {
                it.getEffectivePermissions(client.selfId)
            }.map { Permission.MUTE_MEMBERS in it && Permission.BAN_MEMBERS in it && Permission.MANAGE_MESSAGES in it && Permission.MANAGE_ROLES in it }
                .awaitFirst()

        if (!permission) {
            respond("Solo el administrador puede ocupar este comando")
            return@precondition false
        }

        if (!botPermission) {
            respond("Lucy no tiene los permisos para ejecutar este comando")
            return@precondition false
        }
    }

    true
}

