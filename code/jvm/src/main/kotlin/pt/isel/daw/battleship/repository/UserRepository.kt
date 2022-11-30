package pt.isel.daw.battleship.repository


import pt.isel.daw.battleship.repository.dto.UserDTO
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken

interface UserRepository {

    /**
     * Creates a new user with the given name and password and token.
     * @param user the user to be created
     */
    fun addUser(user: UserDTO): UserID?

    /**
     * Verifies whether the given name is already in use by another user.
     * @param name the name of the user
     * @return true if the name is already in use, false otherwise
     */
    fun hasUser(name: String): Boolean

    /**
     * Checks if the given username and password are valid.
     * @param userName the name of the user
     * @param hashAndSaltedPassword the hashed and salted password of the user
     * @return the [ID] of the user if the credentials are valid, null otherwise
     */
    fun verifyUserCredentials(userName: String, hashAndSaltedPassword: String): AuthInformation?

    /**
     * Gets a [User]'s salt.
     * @param userName the name of the user
     * @return the salt of the user if the user exists, null otherwise
     */
    fun getUserSalt(userName: String): String



    /**
     * Gets the [ID] of the user with the given token.
     * @param token the token of the user
     * @return the [ID] of the user with the given token or null if the user does not exist
     */
    fun getUserIDByToken(token: UserToken): ID?

    /**
     * Gets the [User].
     * @param userID the [ID] of the user
     * @return the [User] with the given [ID] or null if the user does not exist
     */
    fun getUser(userID: ID): User?


}