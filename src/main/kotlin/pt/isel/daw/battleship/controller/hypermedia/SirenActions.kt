package pt.isel.daw.battleship.controller.hypermedia

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType

const val API_URI_BASE = "http://localhost:8080/api"

val loginAction = SirenAction(
    name = "login",
    title = "Login",
    method = HttpMethod.POST.toString(),
    href = "$API_URI_BASE/login",
    type = MediaType.APPLICATION_JSON_VALUE,
    fields = listOf(
        SirenAction.Field(name = "username", type = "text"),
        SirenAction.Field(name = "password", type = "password")
    )
)

val registerAction = loginAction.copy(
    name = "register",
    title = "Register",
    href = "$API_URI_BASE/register"
)

val playIntentAction = SirenAction(
    name = "play-intent",
    title = "Play Intent",
    method = HttpMethod.POST.toString(),
    href = "$API_URI_BASE/lobby",
    type = MediaType.APPLICATION_JSON_VALUE,
    fields = listOf(
        SirenAction.Field(name = "userID", type = "text"),
    )
)

