package pt.isel.daw.battleship.services

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransaction
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.services.dto.toDTO
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransactionFactory
import pt.isel.daw.battleship.utils.UserID
import pt.isel.daw.battleship.utils.UserName

@Component
class GameService(
    private val transactionFactory: TransactionFactory
) {
    /**
     * Gets a game by its id
     */
    fun getGame(gameId: Id, userID: UserID): Game? {
        return transactionFactory.execute {
            val game = gamesRepository.getGame(gameId)
            check(userID == game?.turnID){"Is not user's $userID turn"}
            game
        }
    }

    /**
     * Creates a new game or joins an existing one
     */
    fun createOrJoinGame(userID: UserID): Id?{
        return transactionFactory.execute {
            val waitingStateGame = gamesRepository.getWaitingStateGame()
            val game = waitingStateGame?.beginPlaceShipsStage(userID) ?: Game.new(userID, GameRules.DEFAULT)
            gamesRepository.persist(game.toDTO())
        }
    }
}

data class GameState(val state: Game.State, val winner: User?)
data class GameStatistics(val nGames: Int, val ranking: List<Pair<UserName, Int>>)

private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=craquesdabola123")
        //setURL("jdbc:postgresql://localhost:49153/postgres?user=postgres&password=postgresw")
    }
).configure()

fun testWithTransactionManagerAndRollback(block: (TransactionFactory) -> Unit) = jdbi.useTransaction<Exception>
{ handle ->

    val transaction = JdbiTransaction(handle)

    // a test TransactionManager that never commits
    val transactionManager = object : TransactionFactory {
        override fun <R> execute(block: (Transaction) -> R): R {
            return block(transaction)
        }

    }
    block(transactionManager)

    // finally, we rollback everything
    handle.rollback()
}

fun main(){
    val gameService = GameService(JdbiTransactionFactory(jdbi))
    val rules= GameRules.DEFAULT
    println(gameService.createOrJoinGame(1))
}