package cl.lucy.tea.service

import com.gitlab.kordlib.rest.request.KtorRequestHandler
import com.gitlab.kordlib.rest.service.RestClient
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import service.MemberService

fun DI.MainBuilder.bindServices() {
    bind<MemberService>() with singleton { MemberService() }
    bind<RestClient>() with singleton { RestClient(KtorRequestHandler("NzI3MDAyMDcxMzQ5MzI5OTMw.Xvlffg.Q9cI34neuqdKhW7aeQAP4abFKYw")) }
}