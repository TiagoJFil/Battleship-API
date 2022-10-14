package pt.isel.daw.battleship.services

import org.jdbi.v3.core.Jdbi
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.services.transactions.Transaction
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.services.transactions.jdbi.JdbiTransaction
import pt.isel.daw.battleship.repository.jdbi.configure
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.utils.UserName


class GameService(
    private val transactionFactory: TransactionFactory
) {

    /**
     * Allows a user to define a set of shots on each round.
     */
    fun makeShots(tiles: List<Square>, userId: Id, gameId: Id) {
        return transactionFactory.execute {
            val gameRepo = gamesRepository
            //list because it depends on the number of shots of the game
            val game = gameRepo.getGame(gameId) ?: throw Exception("Game not found")
            val uid = game.turnPlayer.id
            if (uid != userId) throw Exception("Not your turn")

            val newGame = game.makeShot(tiles)

            //boardRepo.updateBoard(gameId, newBoard)
            gameRepo.updateGame(gameId, newGame)
        }
    }

    /**
     * Allow a user to define the layout of their fleet in the grid.
     */
    fun setBoardLayout(shipList: List<ShipInfo>, userId: Id, gameId: Id) {
        return transactionFactory.execute {
            val gameRepo = gamesRepository

            val game = gameRepo.getGame(gameId) ?: throw Exception("Game not found")
            val uid = game.turnPlayer.id
            if (uid != userId) throw Exception("Not your turn")

            val newGame = game.placeShips(shipList)
            gameRepo.updateGame(gameId, newGame)
        }
    }

    /**
     * Gets the number os played games and users ranking
     */
    fun getStatistics(): GameStatistics {
        return transactionFactory.execute {
            val nGames = gamesRepository.getNumOfGames()
            val ranking = usersRepository.getUsersRanking()
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
            val gamesRepository = gamesRepository
            if (gamesRepository.hasGame(gameId)) throw Exception("Game not found")
            if (!gamesRepository.verifyTurn(userId, gameId)) throw Exception("Not your turn")

            val gameState = gamesRepository.getGameState(gameId)

            return@execute GameState(gameState.first, gameState.second)

            //verify game id
            //return game state
        }
    }
}

data class GameState(val state: Game.State, val winner: User?)
data class GameStatistics(val nGames: Int, val ranking: List<Pair<UserName, Int>>)
