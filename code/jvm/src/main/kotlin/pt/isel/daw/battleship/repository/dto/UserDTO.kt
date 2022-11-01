package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken

data class UserDTO(
    val id: UserID?,
    val name: String,
    val hashedPassword: String,
    val token: UserToken
)

