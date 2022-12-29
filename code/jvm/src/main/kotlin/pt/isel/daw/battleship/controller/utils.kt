package pt.isel.daw.battleship.controller

import org.springframework.http.server.ServerHttpResponse
import pt.isel.daw.battleship.controller.controllers.UserController
import pt.isel.daw.battleship.controller.controllers.UserController.Companion.COOKIE_USER_ID_NAME
import pt.isel.daw.battleship.controller.pipeline.authentication.CookieAuthorizationProcessor.Companion.COOKIE_AUTHORIZATION_NAME
import pt.isel.daw.battleship.services.entities.AuthInformation
import javax.servlet.http.Cookie

/**
 * Sets the max age of the cookie
 */
fun Cookie.maxAge(maxAge: Int): Cookie {
    this.maxAge = maxAge
    return this
}

/**
 * Sets the path of the cookie
 */
fun Cookie.path(path: String): Cookie {
    this.path = path
    return this
}

/**
 * Transforms the [AuthInformation] properties into [Cookie]s and returns them
 */
fun AuthInformation.toCookies(): List<Cookie> {
    val authCookie = Cookie(COOKIE_AUTHORIZATION_NAME, token)
        .path("/")
        .maxAge(UserController.COOKIE_LIFETIME)
    val userIDCookie = Cookie(COOKIE_USER_ID_NAME, uid.toString())
        .path("/")
        .maxAge(UserController.COOKIE_LIFETIME)
    return listOf(authCookie, userIDCookie)
}

/**
 * Converts a [Cookie] into a [String] that can be used in a HTTP response header.
 */
fun Cookie.asString() : String {
    val expireDate = java.util.Date(System.currentTimeMillis() + maxAge * 1000)
    var cookieString = "$name=$value"
    maxAge.let { cookieString += "; Max-Age=$it" }
    cookieString += "; Expires=${expireDate}"
    path?.let { cookieString += "; Path=$it" }
    domain?.let { cookieString += "; Domain=$it" }
    return cookieString
}


fun ServerHttpResponse.addCookie(authCookie: Cookie) {
    this.headers.add("Set-Cookie", authCookie.asString())
}
