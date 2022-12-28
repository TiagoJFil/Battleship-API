package pt.isel.daw.battleship.controller

import org.springframework.http.server.ServerHttpResponse
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
 * Converts a [Cookie] into a [String] that can be used in a HTTP response header.
 */
fun Cookie.asString() : String {
    val expireDate = java.util.Date(System.currentTimeMillis() + maxAge * 1000)
    var cookieString = "$name=$value"
    maxAge.let { cookieString += "; Max-Age=$it" }
    cookieString += "; Expires=${expireDate}"
    path?.let { cookieString += "; Path=$it" }
    domain?.let { cookieString += "; Domain=$it" }
    isHttpOnly.let { cookieString += "; HttpOnly" }
    return cookieString
}


fun ServerHttpResponse.addCookie(authCookie: Cookie) {
    this.headers.add("Set-Cookie", authCookie.asString())
}
