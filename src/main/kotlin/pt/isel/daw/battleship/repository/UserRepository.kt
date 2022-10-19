package pt.isel.daw.battleship.repository


import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserName
import pt.isel.daw.battleship.utils.UserToken

interface UserRepository {
    fun getUsersRanking(): List<Pair<UserName, Int>>

    /**
     * Creates a new user with the given name and password and token.
     * @return the [ID] of the new user
     */
    fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): ID

    /**
     * Verifies whether the given name is already in use by another user.
     */
    fun hasUser(name: String): Boolean

    /**
     * Checks if the given user name and password are valid.
     * @return the [ID] of the user if the credentials are valid, null otherwise
     */
    fun loginUser(userName: String, hashedPassword: String): UserToken?

    /**
     * Gets the [ID] of the user with the given token.
     */
    fun getUserIDByToken(token: UserToken): ID?
}