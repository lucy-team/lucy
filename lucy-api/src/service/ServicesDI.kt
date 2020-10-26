package cl.lucy.tea.service

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import service.MemberService

fun DI.MainBuilder.bindServices() {
    bind<MemberService>() with singleton { MemberService() }
}