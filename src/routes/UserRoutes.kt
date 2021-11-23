package ru.skovoroda.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import ru.skovoroda.authentication.JwtService
import ru.skovoroda.data.model.User
import ru.skovoroda.data.model.auth.requests.LoginRequest
import ru.skovoroda.data.model.auth.requests.RegisterRequest
import ru.skovoroda.data.model.auth.responses.AuthResponse
import ru.skovoroda.repository.UserRepo

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"

@Location(REGISTER_REQUEST)
class UserRegisterRoute

@Location(LOGIN_REQUEST)
class UserLoginRoute

fun Route.UserRoutes(
    db: UserRepo,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserRegisterRoute> {
        val registerRequest =
            try {
                call.receive<RegisterRequest>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthResponse(success = false, message = "Missing some fields.")
                )
                println("!!! exception while processing register request")
                return@post
            }

        try {
            val user = User(
                name = registerRequest.name,
                email = registerRequest.email,
                password = hashFunction(registerRequest.password)
            )
            db.addUser(user = user)
            call.respond(HttpStatusCode.OK, AuthResponse(success = true, message = jwtService.generateToken(user)))
        } catch (e: Exception ) {
            call.respond(
                HttpStatusCode.Conflict,
                AuthResponse(success = false, e.message ?: "Some problem occurred.")
            )
        }
    }

    post<UserLoginRoute> {
        val logingRequest =
            try {
                call.receive<LoginRequest>()
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    AuthResponse(success = false, message = "Missing some fields.")
                )
                return@post
            }

        try {
            val user = db.findUserByEmail(email = logingRequest.email)

            if(user == null){
                call.respond(HttpStatusCode.BadRequest, AuthResponse(success = false, message = "Wrong email"))
            }
            else {
                if(user.password == hashFunction(logingRequest.password)){
                    call.respond(HttpStatusCode.OK, AuthResponse(success = true, jwtService.generateToken(user = user)))
                }
                else {
                    call.respond(HttpStatusCode.BadRequest, AuthResponse(success = false, message = "Wrong password"))
                }
            }
        }
        catch (e:Exception){
            call.respond(HttpStatusCode.Conflict, AuthResponse(success = false, message = e.message?:"Some problem occurred"))
        }

    }

}
