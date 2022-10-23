package pt.isel.daw.battleship.repository


import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken

interface UserRepository {

    /**
     * Creates a new user with the given name and password and token.
     * @param userName the name of the user
     * @param userAuthToken the token of the user
     * @param hashedPassword the hashed password of the user
     * @return the [ID] of the new user or null if the user already exists
     */
    fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): UserID?

    /**
     * Verifies whether the given name is already in use by another user.
     * @param name the name of the user
     * @return true if the name is already in use, false otherwise
     */
    fun hasUser(name: String): Boolean

    /**
     * Checks if the given username and password are valid.
     * @param userName the name of the user
     * @param hashedPassword the hashed password of the user
     * @return the [ID] of the user if the credentials are valid, null otherwise
     */
    fun loginUser(userName: String, hashedPassword: String): AuthInformation?

    /**
     * Gets the [ID] of the user with the given token.
     * @param token the token of the user
     * @return the [ID] of the user with the given token or null if the user does not exist
     */
    fun getUserIDByToken(token: UserToken): ID?

}