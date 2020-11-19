package cl.lucy.tea.route

import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di

fun Route.guild() {

    val client by di().instance<RestClient>()

    route("/guild") {
        get {
            val id = call.request.queryParameters["id"].toString()
            call.respond(client.guild.getGuild(id))
        }


    }
}