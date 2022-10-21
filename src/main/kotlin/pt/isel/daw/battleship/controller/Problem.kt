package pt.isel.daw.battleship.controller

import java.net.URI

class Problem(
    typeUri: URI
) {
    companion object{
        val userAlreadyExists = Problem(
            URI(
                ""
            )
        )
    }
}