package cl.lucy.tea.route

import cl.lucy.tea.model.Role
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.rest.service.RestClient
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.di
import java.awt.Color

fun Route.role() {

    val client by di().instance<RestClient>()

    route("/role/{guildId}") {

        get {
            val guildId = call.parameters["guildId"].toString()
            call.respond(client.guild.getGuildRoles(guildId))
        }

        post {
            val guildId = call.parameters["guildId"].toString()
            val role = call.receive<Role>()

            val response = client.guild.createGuildRole(guildId) {
                color = Color.getColor(role.color) // Ver como obtener el color de un hex
                name = role.name
                mentionable = true
            }

            call.respond(HttpStatusCode.Created, response)
        }

        put("/moderator/{roleId}") {
            val roleId = call.parameters["roleId"].toString()
            val guildId = call.parameters["guildId"].toString()

            client.guild.modifyGuildRole(guildId, roleId) {
                permissions = Permissions(0) // Calcular el codigo para dar permisos de administrador
            }

            call.respond(HttpStatusCode.OK)
        }


        delete("/{roleId}") {
            val roleId = call.parameters["roleId"].toString()
            val guildId = call.parameters["guildId"].toString()

            client.guild.deleteGuildRole(guildId, roleId)

            call.respond(HttpStatusCode.NoContent)
        }

    }
}