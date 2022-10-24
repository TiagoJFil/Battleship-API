package pt.isel.daw.battleship.services


import org.springframework.stereotype.Component
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.repository.dto.*
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
     * Gets the game state of the game with the given id
     * @param gameId the id of the game
     * @return [Game.State] the game state
     */
    fun getGameState(gameId: Id, userID: UserID): Game.State {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameId)
            game ?: throw GameNotFoundException(gameId)
            if(userID !in game.boards.keys) throw ForbiddenAccessAppException("User $userID is not part of the game $gameId")
            game.state
        }
    }

    /**
     * Creates a new game or joins an existing one
     * @param userID the user that is creating/joining the game
     * @return [Id] of the game created/joined or [Null] if the user joined the queue and is waiting for a game
     * @throws InternalErrorAppException if an error occurs while creating/joining the game
     */
    fun createOrJoinGame(userID: UserID): Id? =
        transactionFactory.execute {
            val pairedPlayerID = lobbyRepository.getWaitingPlayer()

            if (pairedPlayerID == null || pairedPlayerID == userID) {
                lobbyRepository.addPlayerToLobby(userID)
                return@execute null
            }

            lobbyRepository.removePlayerFromLobby(pairedPlayerID)
            val newGame = Game.new(userID to pairedPlayerID, GameRules.DEFAULT)
            gamesRepository.persist(newGame.toDTO()) ?: throw InternalErrorAppException()
        }


    /**
     * Leaves the lobby
     * @param userID the user that is leaving the lobby
     */
    fun leaveLobby(userID: UserID) =
        transactionFactory.execute {
            if(!lobbyRepository.removePlayerFromLobby(userID))
                throw ForbiddenAccessAppException("User $userID is not in the lobby")
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
                gamesRepository.get(gameId) ?: throw GameNotFoundException(gameId)
            if (userID !in currentState.boards.keys) throw ForbiddenAccessAppException("You are not allowed to make shots in this game")
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
     * @throws GameNotFoundException if the game with the given id does not exist
     * @throws ForbiddenAccessAppException if the user is not in the game
     */
    fun defineFleetLayout(userID: UserID, gameId: Id, ships: List<ShipInfo>) {
        transactionFactory.execute {
            val currentState =
                gamesRepository.get(gameId) ?: throw GameNotFoundException(gameId)
            if (userID !in currentState.boards.keys) throw ForbiddenAccessAppException("You are not allowed to define the layout in this game")
            val newState = currentState.placeShips(ships, userID)
            gamesRepository.persist(newState.toDTO())
        }
    }

    /**
     * Gets the fleet state of a user in a game
     * @param userID the user that is getting the fleet state
     * @param gameId the id of the game
     * @param opponentFleet if true, returns the fleet state of the opponent, otherwise returns the fleet state of the user
     * @return [BoardDTO]
     */
    fun getFleet(userID: UserID, gameId: Id, opponentFleet: Boolean): BoardDTO? {
        return transactionFactory.execute {
            val game =  gamesRepository.get(gameId) ?: throw GameNotFoundException(gameId)
            if (userID !in game.boards.keys) throw ForbiddenAccessAppException("You are not allowed to access this game")
            game.boards[userID] ?: throw ForbiddenAccessAppException("You are not allowed to see this game")

            val user = if (opponentFleet) game.boards.keys.first { it != userID } else userID
            val board = game.boards[user] ?: throw ForbiddenAccessAppException("You are not allowed to see this game")
            return@execute board.toDTO(user)
        }
    }


}

