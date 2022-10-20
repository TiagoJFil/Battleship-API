package pt.isel.daw.battleship.services

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.services.dto.toDTO
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.utils.UserID

@Component
class GameService(
    private val transactionFactory: TransactionFactory
) {

    /**
     * Creates a new game or joins an existing one
     * @param userID the user that is creating/joining the game
     * @return Id of the game created/joined
     */
    fun createOrJoinGame(userID: UserID): Id {
        return transactionFactory.execute {
            val waitingStateGame = gamesRepository.getWaitingStateGame()
            val game = waitingStateGame?.beginPlaceShipsStage(userID) ?: Game.new(userID, GameRules.DEFAULT)
            gamesRepository.persist(game.toDTO()) ?: throw InternalError()
        }
    }

    /**
     * Makes a set of shots to the board of the game with the given id
     * @param userID the user that is making the shots
     * @param gameId the id of the game
     * @param shots the shots to be made
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

    /**
     * Places a fleet in the board of the game with the given id
     * @param userID the user that is placing the fleet
     * @param gameId the id of the game
     * @param fleet the fleet to be placed
     */
    fun defineFleetLayout(userID: UserID, gameId: Id, ships: List<ShipInfo>) {
        transactionFactory.execute {
            val currentState =
                gamesRepository.getGame(gameId) ?: throw IllegalArgumentException("Not found") // TODO: NotFound
            val newState = currentState.placeShips(ships, userID)
            gamesRepository.persist(newState.toDTO())

            //testing purposes
            gamesRepository.getGame(gameId)?.boards?.values?.forEach { println(it?.pretty()); println("------------------------------------------------------") }
        }
    }
}

