package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.repository.dto.UserDTO
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserToken


class JdbiUsersRepository(val handle: Handle) : UserRepository {

    /**
     * Creates a new user with the given name and password and token.
     * @param user the user to be created
     * @return the [ID] of the new user or null if the user already exists
     */
    override fun addUser(user: UserDTO): UserID? {
        val uid = handle.createUpdate("insert into \"User\" (name, password,salt) values (:userName,:password,:salt) returning id")
            .bind("userName", user.name)
            .bind("password", user.hashedPassword)
            .bind("salt", user.salt)
            .executeAndReturnGeneratedKeys("id")
            .mapTo(Int::class.java)
            .first()

        uid?.run {
            handle.createUpdate("insert into token (userid, token) values (:id, :token)")
                .bind("id", uid)
                .bind("token", user.token)
                .execute()
            user.token
        }

        return uid
    }

    /**
     * Verifies whether the given name is already in use by another user.
     * @param name the name of the user
     * @return true if the name is already in use, false otherwise
     */
    override fun hasUser(name: String): Boolean {
        return handle.createQuery("select count(*) from \"User\" where name = :name")
            .bind("name", name)
            .mapTo(Int::class.java)
            .first() == 1
    }

    /**
     * Checks if the given username and password are valid.
     * @param userName the name of the user
     * @param hashAndSaltedPassword the hashed password of the user
     * @return the [ID] of the user if the credentials are valid, null otherwise
     */
    override fun verifyUserCredentials(userName: String, hashAndSaltedPassword: String): AuthInformation? {
        return handle.createQuery("""
             select u.id as uid, t.token from "User" u join token t on u.id = t.userid where u.name = :name and u.password = :password
        """).bind("name", userName)
            .bind("password", hashAndSaltedPassword)
            .mapTo(AuthInformation::class.java)
            .firstOrNull()
    }
    /**
     * Gets a user's salt.
     * @param userName the name of the user
     * @return the salt of the user if the user exists, null otherwise
     */
    override fun getUserSalt(userName: String): String {
        return handle.createQuery("select salt from \"User\" where name = :name")
            .bind("name", userName)
            .mapTo(String::class.java)
            .first()
    }

    /**
     * Gets the [ID] of the user with the given token.
     * @param token the token of the user
     * @return the [ID] of the user with the given token or null if the user does not exist
     */
    override fun getUserIDByToken(token: UserToken): ID? {
        return handle.createQuery("""
            select userid from token where token = :token    
        """).bind("token", token)
            .mapTo(Int::class.java)
            .firstOrNull()
    }
    /**
     * Gets the user information.
     * @param userID The user ID to get.
     * @return [User] with the user information.
     */
    override fun getUser(userID: ID): User? {
        return handle.createQuery("""
            select name from "User" where id = :id
        """).bind("id", userID)
            .mapTo(User::class.java)
            .firstOrNull()
    }
}
