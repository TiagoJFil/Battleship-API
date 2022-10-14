package pt.isel.daw.battleship.services

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.battleship.data.model.*
import pt.isel.daw.battleship.data.model.Game.*
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransaction
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.utils.UserName

class GameService(
    private val transactionFactory: TransactionFactory
) {

    /**
     * Allow a user to define a set of shots on each round.
     */
    fun makeShots(/*tiles: List<Square>, userId: Id, gameId: Id*/) {
        return transactionFactory.execute {
            val gameRepo = it.gamesRepository

            println(gameRepo.hasGame(10))
            //list because it depends on the number of shots of the game
           // val game = gameRepo.getGame(gameId) ?: throw Exception("Game not found")
            //val uid = game.turnPlayer.id
            //if (uid != userId) throw Exception("Not your turn")

            //val newGame = game.makeShot(tiles)

            //boardRepo.updateBoard(gameId, newBoard)
            //gameRepo.updateGame(gameId, newGame)
        }
    }

    /**
     * Allow a user to define the layout of their fleet in the grid.
     */
    fun setBoardLayout(shipList: List<ShipInfo>, userId: Id, gameId: Id) {
        return transactionFactory.execute {
            val gameRepo = it.gamesRepository

            //val game = gameRepo.getGame(gameId) ?: throw Exception("Game not found")
            //val uid = game.turnPlayer.id
            //if (uid != userId) throw Exception("Not your turn")

           // val newGame = game.placeShips(shipList)
           // gameRepo.updateGame(gameId, newGame)
        }
    }

    /**
     * Gets the number os played games and users ranking
     */
    fun getStatistics(): GameStatistics {
        return transactionFactory.execute {
            val nGames = it.gamesRepository.getNumOfGames()
            val ranking = it.usersRepository.getUsersRanking()
            return@execute GameStatistics(nGames, ranking)
        }
    }




    fun queueGame(user: Id) {

        //repo.getUser(user)
        //verify user id

        //ver se tem algum game running com esse user
        //queue user
    }

    fun cancelQueue(user: Id) {

        //verify user id
        //ver se o user ta queued
        //remove user from queue
    }

    fun getEnemyFleetState(gameId: Id, userId: Id){

    }

    fun getFleetState(gameId: Id, userId: Id){

    }

    fun getGameState(userId: Id, gameId : Id): GameState{
        //verificaçoes

        return transactionFactory.execute {
            val gamesRepository = it.gamesRepository

            if (gamesRepository.hasGame(gameId)) throw Exception("Game not found")
            //if (!gamesRepository.verifyTurn(userId, gameId)) throw Exception("Not your turn")

            val gameState = gamesRepository.getGameState(gameId)
            return@execute GameState(gameState.first, gameState.second)
        }
    }
}

data class GameState(val state: State, val winner: User?)
data class GameStatistics(val nGames: Int, val ranking: List<Pair<UserName, Int>>)
data class User(val id: Id, val name: String)
data class UserCreateInput(val name: String, val password: String)









private val jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=craquesdabola123")
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
    testWithTransactionManagerAndRollback {
        val gameServices = GameService(it)
        gameServices.makeShots()
    }
}