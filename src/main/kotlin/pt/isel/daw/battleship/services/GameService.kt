package pt.isel.daw.battleship.services

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.model.*
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.services.exception.ForbiddenAccessAppException
import pt.isel.daw.battleship.services.exception.GameNotFoundException
import pt.isel.daw.battleship.services.exception.InternalErrorAppException
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.utils.UserID

@Component
class GameService(
    private val transactionFactory: TransactionFactory
) {

    /**
     * Creates a new game or joins an existing one
     * @param userID the user that is creating/joining the game
     * @return [Result] id of the game created/joined
     * @throws InternalErrorAppException if an error occurs while creating/joining the game
     */
    fun createOrJoinGame(userID: UserID): Result<Id?> = result {

        return@result transactionFactory.execute {
            val pairedPlayerID = lobbyRepository.getWaitingPlayer()

            if(pairedPlayerID == null || pairedPlayerID == userID) {
                lobbyRepository.addPlayerToLobby(userID)
                return@execute null
            }

            lobbyRepository.removePlayerFromLobby(pairedPlayerID)
            val newGame = Game.new(userID to pairedPlayerID, GameRules.DEFAULT)
            gamesRepository.persist(newGame.toDTO()) ?: throw InternalErrorAppException()
        }
    }

    /**
     * Leaves the lobby
     * @param userID the user that is leaving the lobby
     */
    fun leaveLobby(userID: UserID): Result<Boolean> = result {
        transactionFactory.execute {
            lobbyRepository.removePlayerFromLobby(userID)
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
                gamesRepository.getGame(gameId) ?: throw GameNotFoundException(gameId)
            if(  userID !in currentState.boards.keys ) throw ForbiddenAccessAppException("You are not allowed to make shots in this game")
            check(userID == currentState.turnID) { "Not your turn!" }
            val newGameState = currentState.makePlay(shots)
            gamesRepository.persist(newGameState.toDTO())
        }
    }

    /**
     * Places a fleet in the board of the game with the given id
     * @param userID the user that is placing the fleet
     * @param gameId the id of the game
     * @param ships the fleet to be placed
     */
    fun defineFleetLayout(userID: UserID, gameId: Id, ships: List<ShipInfo>) {
        transactionFactory.execute {
            val currentState =
                gamesRepository.getGame(gameId) ?: throw GameNotFoundException(gameId)
            if(  userID !in currentState.boards.keys ) throw ForbiddenAccessAppException("You are not allowed to make shots in this game")
            val newState = currentState.placeShips(ships, userID)
            gamesRepository.persist(newState.toDTO())

            gamesRepository.getGame(gameId)
                ?.boards?.values
                ?.forEach {
                    println(it.pretty())
                    println("------------------------------------------------------")
                }
        }
    }
}

