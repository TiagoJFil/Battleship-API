package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.services.User
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserToken

class JdbiUsersRepository(
    private val handle: Handle
): UserRepository {
    override fun getUsersRanking(): List<Pair<User, Int>> {
        TODO("Not yet implemented")
    }

    override fun addUser(userName: String, userAuthToken: UserToken, hashedPassword: String): ID {
        TODO("Not yet implemented")
    }

}