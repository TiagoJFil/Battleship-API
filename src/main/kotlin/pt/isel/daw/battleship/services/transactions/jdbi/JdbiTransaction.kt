package pt.isel.daw.battleship.services.transactions.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.BoardRepository
import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.repository.jdbi.JdbiBoardsRepository
import pt.isel.daw.battleship.repository.jdbi.JdbiGamesRepository
import pt.isel.daw.battleship.repository.jdbi.JdbiUsersRepository
import pt.isel.daw.battleship.services.transactions.Transaction

class JdbiTransaction(
    private val handle: Handle
) : Transaction {

    override val gamesRepository: GameRepository by lazy { JdbiGamesRepository(handle) }
    override val boardRepository: BoardRepository by lazy { JdbiBoardsRepository(handle) }
    override val usersRepository: UserRepository by lazy { JdbiUsersRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}