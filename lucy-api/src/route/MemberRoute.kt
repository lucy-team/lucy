package cl.lucy.tea.route

import cl.lucy.tea.toUUID
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import model.Member
import org.kodein.di.instance
import org.kodein.di.ktor.di
import service.MemberService

fun Route.member() {

    val memberService by di().instance<MemberService>()

    route("/member") {
        get {
            val id = call.request.queryParameters["id"]

            if (id != null) {
                val member = memberService.get(id.toString().toUUID())
                call.respond(member)
            } else {
                val allMembers = memberService.all()
                call.respond(allMembers)
            }
        }

        post {
            val member = call.receive<Member>()
            val guildId = call.request.queryParameters["guildId"].toString().toUUID()

            call.respond(HttpStatusCode.Created, memberService.new(guildId, member))
        }

        delete {
            val id = call.request.queryParameters["id"].toString().toUUID()
            memberService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }

        put {
            val member = call.receive<Member>()
            call.respond(HttpStatusCode.OK, memberService.update(member))

        }
    }
}