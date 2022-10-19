package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserName
import pt.isel.daw.battleship.utils.UserToken


class JdbiUsersRepository(
    private val handle: Handle
): UserRepository {
    //Gets the ranking of the users with most wins
    override fun getUsersRanking(): List<Pair<UserName, Int>> {

        //TODO("change the name to id")
        return handle.createQuery("""
            SELECT "User".name, COUNT(*) as gamesWon 
            FROM "User" JOIN game ON "User".id = game.winner""".trimIndent() +
                """ GROUP BY "User".name ORDER BY gamesWon DESC""")
            .map { rs, _, _ ->
                    val userName = rs.getString("name")
                    val gamesWon = rs.getInt("gamesWon")
                    return@map Pair(userName, gamesWon)
            }.toList()
    }


    /**
     * Creates a new user with the given name and password and token.
     * @return the [ID] of the new user
     */
    override fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): ID {
        val userId = handle.createUpdate("""
            INSERT INTO "User" (name, password) 
            VALUES (:name, :password)""")
            .bind("name", userName)
            .bind("password", hashedPassword)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

        handle.createUpdate("""
             INSERT INTO token (userId,token) 
             VALUES (userid,:token)   """)
            .bind("token",userAuthToken)
            .execute()
        return userId
    }

    /**
     * Verifies whether the given name is already in use by another user.
     */
    override fun hasUser(name: String): Boolean {
        return handle.createQuery("""
            SELECT COUNT(*) FROM "User" 
            WHERE name = :name""")
            .bind("name", name)
            .mapTo(Int::class.java)
            .one() > 0
    }

    /**
     * Checks if the given user name and password are valid.
     * @return the [ID] of the user if the credentials are valid, null otherwise
     */
    override fun loginUser(userName: String, hashedPassword: String): UserToken? {
        return handle.createQuery("""
            SELECT token FROM token 
            WHERE userId = (SELECT id FROM "User" WHERE name = :name AND password = :password)""")
            .bind("name", userName)
            .bind("password", hashedPassword)
            .mapTo(String::class.java)
            .one()
    }

    /**
     * Gets the [ID] of the user with the given token.
     */
    override fun getUserIDByToken(token: UserToken): ID? {
        return handle.createQuery("""
            SELECT userId FROM token 
            WHERE token = :token""")
            .bind("token", token)
            .mapTo(Int::class.java)
            .one()
    }
}