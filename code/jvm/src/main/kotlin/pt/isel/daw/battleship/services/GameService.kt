package pt.isel.daw.battleship.services


import org.springframework.stereotype.Component
import pt.isel.daw.battleship.controller.dto.BoardDTO
import pt.isel.daw.battleship.controller.dto.toDTO
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.board.ShipInfo
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.services.entities.GameStateInfo
import pt.isel.daw.battleship.services.entities.LobbyInformation
import pt.isel.daw.battleship.services.exception.*
import pt.isel.daw.battleship.services.transactions.TransactionFactory
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID


@Component
class GameService(
    private val transactionFactory: TransactionFactory
) {

    companion object {
        private const val TOOK_TOO_LONG_PLACING_SHIPS = "You took too long to place your ships"
        private const val TOOK_TOO_LONG_MAKING_SHOTS = "You took too long to make your shots"
        private const val MUST_BE_PARTICIPANT = "You must be a participant of the game to perform this action"
    }

    /**
     * Gets the game state of the game with the given id
     * @param gameID the id of the game
     * @return [Game.State] the game state
     */
    fun getGameState(gameID: ID, userID: UserID): GameStateInfo {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            if(userID !in game.userToBoards.keys) throw ForbiddenAccessAppException("User $userID is not part of the game $gameID")

            GameStateInfo(
                game.state,
                game.winnerId
            )
        }
    }

    /**
     * Creates a new game or joins an existing one
     * @param userID the user that is creating/joining the game
     * @return LobbyInformation the information about the lobby joined
     * @throws InternalErrorAppException if an error occurs while creating/joining the game
     */
    fun enqueue(userID: UserID) =
        transactionFactory.execute {
            val lobbyDto = lobbyRepository.findWaitingLobby(userID)

            if (lobbyDto == null) {
                val lobbyID = lobbyRepository.createLobby(userID)
                return@execute LobbyInformation(lobbyID, null)
            }


            val newGame = Game.new(lobbyDto.player1 to userID, GameRules.DEFAULT)
            val gameID = gamesRepository.persist(newGame.toDTO())

            if (!lobbyRepository.completeLobby(lobbyDto.id, userID, gameID))
                throw InternalErrorAppException()

            return@execute LobbyInformation(lobbyDto.id, gameID)
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
    fun makeShots(userID: UserID, gameId: ID, shots: List<Square>) {
        transactionFactory.execute {
            val currentState =
                gamesRepository.get(gameId) ?: throw GameNotFoundException(gameId)
            if (userID !in currentState.userToBoards.keys) throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)
            if (userID != currentState.turnID) throw ForbiddenAccessAppException("Not your turn!")

            val newGameState = currentState.makePlay(shots)
            gamesRepository.persist(newGameState.toDTO())

            if (newGameState.state == Game.State.CANCELLED) {
                throw TimeoutExceededAppException(TOOK_TOO_LONG_MAKING_SHOTS)
            }
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
    fun defineFleetLayout(userID: UserID, gameId: ID, ships: List<ShipInfo>) {
        transactionFactory.execute {
            val currentState = gamesRepository.get(gameId) ?: throw GameNotFoundException(gameId)
            if (userID !in currentState.userToBoards.keys) throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)

            val newGameState = currentState.placeShips(ships, userID)
            gamesRepository.persist(newGameState.toDTO())

            if (newGameState.state == Game.State.CANCELLED) {
                throw TimeoutExceededAppException(TOOK_TOO_LONG_PLACING_SHIPS)
            }
        }
    }

    enum class Fleet { MY, OPPONENT }

    /**
     * Gets the fleet state of a user in a game
     * @param userID the user that is getting the fleet state
     * @param gameId the id of the game
     * @param whichFleet the fleet to be retrieved
     * @return [BoardDTO]
     */
    fun getFleetState(userID: UserID, gameId: ID, whichFleet: String): BoardDTO {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameId) ?: throw GameNotFoundException(gameId)
            game.userToBoards[userID] ?: throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)

            val fleetState = when (whichFleet) {
                "my" -> Fleet.MY
                "opponent" -> Fleet.OPPONENT
                else -> throw NotFoundAppException("Fleet $whichFleet not found")
            }

            val fleetOwnerID = when (fleetState) {
                Fleet.MY -> userID
                Fleet.OPPONENT -> game.userToBoards.keys.first { it != userID }
            }

            val board = game.userToBoards[fleetOwnerID] ?: throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)
            return@execute board.toDTO(fleetOwnerID, fleetState)
        }
    }

    /**
     * Gets the lobby state of the lobby identified by the given [lobbyId]
     */
    fun getMyLobbyState(userID: UserID, lobbyId: ID): LobbyInformation {
        return transactionFactory.execute {
            val lobbyDto = lobbyRepository.get(lobbyId) ?: throw NotFoundAppException("Lobby $lobbyId")
            if(userID !in listOf(lobbyDto.player1, lobbyDto.player2))
                throw ForbiddenAccessAppException("You're not allowed to access this lobby")

            return@execute LobbyInformation(
                lobbyId,
                lobbyDto.gameID
            )
        }
    }


}

