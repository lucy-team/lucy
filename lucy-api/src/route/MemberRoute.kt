package cl.lucy.tea.route

import cl.lucy.tea.toUUID
import com.gitlab.kordlib.rest.service.RestClient
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

    val client by di().instance<RestClient>()

    route("/member") {
        get {
            val id = call.request.queryParameters["id"]

            if (id != null) {
                val member = memberService.get(id.toLong())
                call.respond(member)
            } else {
                val allMembers = memberService.all()
                call.respond(allMembers)
            }
        }

        post {
            val member = call.receive<Member>()
            val guildId = call.request.queryParameters["guildId"].toString()

            // Se agregar del discord el usuario
            client.guild.addGuildMember(guildId, member.id.toString()) { }

            // Se agregar a la base de datos
            call.respond(HttpStatusCode.Created, memberService.new(guildId.toLong(), member))
        }

        delete {
            val id = call.request.queryParameters["id"].toString()
            val guildId = call.request.queryParameters["guildId"].toString()

            // Se elimina del discord el usuario
            client.guild.deleteGuildMember(guildId, id)

            // Se eliminar de la bd
            memberService.delete(id.toLong())

            call.respond(HttpStatusCode.NoContent)
        }

        put {
            val member = call.receive<Member>()
            call.respond(HttpStatusCode.OK, memberService.update(member))
        }
    }
}