package pt.isel.daw.battleship.services.validationEntities

import pt.isel.daw.battleship.services.requireParameter

/**
 * Represents a user Authentication/Registration request.
 * Verifies the validity of the request.
 * @param username the username of the user
 * @param password the password of the user
 */
class UserValidation(
    username: String?,
    password: String?,
) {

    val username: String
    val password: String

    init {
        val safeUsername = requireParameter(username, "username")
        val safePassword = requireParameter(password, "password")

        requireParameter(safePassword.length >= 5) { "Password must be at least 5 characters long" }
        requireParameter(safePassword.contains(Regex("[0-9]"))) { "Password must contain at least one digit" }
        requireParameter(safeUsername.length >= 3) { "Username must be at least 3 characters long" }
        requireParameter(safeUsername.length <= 30) { "Username must be at most 30 characters long" }

        this.username = safeUsername
        this.password = safePassword
    }

}
