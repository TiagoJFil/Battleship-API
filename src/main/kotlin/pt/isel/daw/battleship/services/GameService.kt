package pt.isel.daw.battleship.services

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.utils.UserID

@Component
class GameService(
    private val transactionFactory: TransactionFactory
) {

    /**
     * Creates a new game or joins an existing one
     */
    fun createOrJoinGame(userID: UserID): Result<Id> = result {


        return@result transactionFactory.execute {
            val waitingStateGame = gamesRepository.getWaitingStateGame()
            val newGameState = waitingStateGame?.let { safeGame ->
                safeGame.copy(
                    state = Game.State.PLACING_SHIPS,
                    boards = listOf(safeGame.turnID, userID).associateWith { Board.empty(safeGame.rules.boardSide) }
                )
            } ?: Game.new(userID, GameRules.DEFAULT)

            gamesRepository.persist(newGameState.toDTO()) ?: throw InternalError()
        }


    }

    /**
     *
     */
    fun makeShots(userID: UserID, gameId: Id, shots: List<Square>) {
        transactionFactory.execute {
            val currentState =
                gamesRepository.getGame(gameId) ?: throw IllegalArgumentException("Not found") // TODO: NotFound
            check(userID == currentState.turnID) { "Not your turn!" }
            val newGameState = currentState.makePlay(shots)
            gamesRepository.persist(newGameState.toDTO())
        }
    }


    fun defineFleetLayout(userID: UserID, gameId: Id, ships: List<ShipInfo>) {
        transactionFactory.execute {
            val currentState =
                gamesRepository.getGame(gameId) ?: throw IllegalArgumentException("Not found") // TODO: NotFound
            val newState = currentState.placeShips(ships, userID)
            gamesRepository.persist(newState.toDTO())

            gamesRepository.getGame(gameId)
                ?.boards?.values
                ?.forEach {
                    println(it?.pretty())
                    println("------------------------------------------------------")
                }
        }
    }


}

