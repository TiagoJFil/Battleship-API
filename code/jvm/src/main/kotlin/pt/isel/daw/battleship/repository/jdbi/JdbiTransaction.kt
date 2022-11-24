package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.repository.GameRepository
import pt.isel.daw.battleship.repository.GeneralRepository
import pt.isel.daw.battleship.repository.LobbyRepository
import pt.isel.daw.battleship.repository.UserRepository
import pt.isel.daw.battleship.services.transactions.Transaction

class JdbiTransaction(
    private val handle: Handle,
) : Transaction {

    override val gamesRepository: GameRepository by lazy { JdbiGamesRepository(handle) }
    override val userRepository: UserRepository by lazy { JdbiUsersRepository(handle) }
    override val lobbyRepository: LobbyRepository by lazy { JdbiLobbyRepository(handle) }
    override val generalRepository: GeneralRepository by lazy { JdbiGeneralRepository(handle) }

    override fun rollback() {
        handle.rollback()
    }
}