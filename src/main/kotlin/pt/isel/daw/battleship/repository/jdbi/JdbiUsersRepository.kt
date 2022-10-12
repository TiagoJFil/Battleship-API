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
        return handle.createQuery("""SELECT "User".name, COUNT(*) as gamesWon FROM "User" JOIN game ON "User".id = game.winner""" +
                """ GROUP BY "User".name ORDER BY gamesWon DESC""")
            .map { rs, _, _ ->
                    val userName = rs.getString("name")
                    val gamesWon = rs.getInt("gamesWon")
                    return@map Pair(userName, gamesWon)
            }.toList()
    }

    override fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): ID {
        TODO("Not yet implemented")
    }
}