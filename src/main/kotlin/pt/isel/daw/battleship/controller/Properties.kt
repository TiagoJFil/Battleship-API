package pt.isel.daw.battleship.controller

class Properties {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val host = System.getenv("HOST") ?: "localhost"
}