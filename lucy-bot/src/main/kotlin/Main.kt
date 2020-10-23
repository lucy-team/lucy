@file:AutoWired

import bot.bot
import bot.on
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.model.prefix.prefix
import discord4j.core.event.domain.VoiceStateUpdateEvent
import kapt.kotlin.generated.configure
import model.music.MusicManager
import command.prefix.db
import command.prefix.lucy
import org.koin.core.inject
import reactor.core.publisher.Mono
import util.memberCount

suspend fun main() = bot("NzI3MDAyMDcxMzQ5MzI5OTMw.Xvlffg.Q9cI34neuqdKhW7aeQAP4abFKYw") {
    configure()

    lucy.on<VoiceStateUpdateEvent> {
        val music : MusicManager by inject()
        val userId = current.userId
        val guildId = current.guildId

        if (userId == client.selfId) {
            if (!current.channelId.isPresent && old.isPresent) {
                music.remove(guildId.asString())
            }
        } else {
            Mono.defer {
                Mono.justOrEmpty(music[guildId.asString()])
                    .flatMap { guildMusic ->
                        memberCount(this, guildId)
                            .filter { memberCount -> (memberCount == 0L) != guildMusic.isLeavingScheduled() }
                            .map { memberCount ->
                                println("1")
                                println("count: ${memberCount}")
                                if (memberCount == 0L && !guildMusic.isLeavingScheduled()) {
                                    println("1.1")
                                    guildMusic.player.isPaused = true
                                    guildMusic.handleLeave()
                                } else if (memberCount != 0L && guildMusic.isLeavingScheduled()) {
                                    println("1.2")
                                    guildMusic.player.isPaused = false
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
}


