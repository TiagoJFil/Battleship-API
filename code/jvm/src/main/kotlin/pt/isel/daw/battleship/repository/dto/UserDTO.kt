package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken

/**
 * Data transfer object for the user
 */
data class UserDTO(
    val id: UserID?=null,
    val name: String,
    val hashedPassword: String,
    val token: UserToken,
    val salt: String
)

