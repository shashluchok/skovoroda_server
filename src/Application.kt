package ru.skovoroda

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.locations.*
import ru.skovoroda.authentication.JwtService
import ru.skovoroda.authentication.hash
import ru.skovoroda.repository.DatabaseFactory
import ru.skovoroda.repository.UserRepo
import ru.skovoroda.routes.UserRoutes

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()

    val db = UserRepo()
    val jwtService = JwtService()
    val hashFunction = { s:String -> hash(s) }

    install(Locations)

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        gson (ContentType.Text.Plain)
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }
        UserRoutes(db = db,jwtService = jwtService, hashFunction = hashFunction)
    }
}

data class MySession(val count: Int = 0)

