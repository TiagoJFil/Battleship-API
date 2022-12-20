package pt.isel.daw.battleship.controller

import javax.servlet.http.Cookie

fun Cookie.maxAge(maxAge: Int): Cookie {
    this.maxAge = maxAge
    return this
}

fun Cookie.path(path: String): Cookie {
    this.path = path
    return this
}