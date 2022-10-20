package pt.isel.daw.battleship.services.transactions.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken


class JdbiUsersRepository(val handle: Handle) : UserRepository {

    /**
     * Creates a new user with the given name and password and token.
     * @return the [ID] of the new user
     */
    override fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): UserID? {
        val uid = handle.createUpdate("insert into \"User\" (name, password) values (:userName,:password) returning id")
            .bind("userName", userName)
            .bind("password", hashedPassword)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Int::class.java)
            .first()

        uid?.run {
            handle.createUpdate("insert into token (userid, token) values (:id, :token)")
                .bind("id", uid)
                .bind("token", userAuthToken)
                .execute()
            userAuthToken
        }

        return uid
    }

    /**
     * Verifies whether the given name is already in use by another user.
     */
    override fun hasUser(name: String): Boolean {
        TODO("Not yet implemented")
    }

    /**
     * Checks if the given user name and password are valid.
     * @return the [ID] of the user if the credentials are valid, null otherwise
     */
    override fun loginUser(userName: String, hashedPassword: String): UserToken? {
        TODO("Not yet implemented")
    }

    /**
     * Gets the [ID] of the user with the given token.
     */
    override fun getUserIDByToken(token: UserToken): ID? {
        TODO("Not yet implemented")
    }


}
