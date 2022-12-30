package pt.isel.daw.battleship.services


import org.springframework.stereotype.Component
import pt.isel.daw.battleship.controller.dto.BoardDTO
import pt.isel.daw.battleship.controller.dto.EmbeddableGameListDTO
import pt.isel.daw.battleship.controller.dto.GameListDTO
import pt.isel.daw.battleship.controller.dto.toDTO
import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.domain.board.ShipInfo
import pt.isel.daw.battleship.repository.dto.toDTO
import pt.isel.daw.battleship.services.entities.*
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
        private const val THIS_GAME_HAS_BEEN_CANCELLED = "This game has been cancelled"
        private const val MUST_BE_PARTICIPANT = "You must be a participant of the game to perform this action"
        private const val NOT_ALLOWED = "You are not allowed to access this resource"
    }

    /**
     * Gets the game state of the game with the given id
     * @param gameID the id of the game
     * @return [Game.State] the game state
     */
    fun getGameState(gameID: ID, userID: UserID, embedded: Boolean = false): EmbeddableGameStateInfo {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            if(userID !in game.playerBoards.keys)
                throw ForbiddenAccessAppException("User $userID is not part of the game $gameID")
            val player1 =if(embedded) {
                userRepository.getUser(game.player1ID)
            } else null
            val player2 = if(embedded) {
                userRepository.getUser(game.player2ID)
            } else null

            EmbeddableGameStateInfo(
                GameStateInfo(
                    game.state,
                    game.turnID,
                    game.player1ID,
                    game.player2ID,
                ),
                player1,
                player2
            )
        }
    }

    /**
     * Creates a new game or joins an existing one
     * @param userID the user that is creating/joining the game
     * @return LobbyInformation the information about the lobby joined
     * @throws InternalErrorAppException if an error occurs while creating/joining the game
     */
    fun enqueue(userID: UserID): LobbyInformation {
        return transactionFactory.execute {
            val lobbyDto = lobbyRepository.findWaitingLobby(userID)

            if (lobbyDto == null) {
                val lobbyID = lobbyRepository.createLobby(userID)
                return@execute LobbyInformation(lobbyID, null)
            }


            val newGame = Game.new(lobbyDto.player1 to userID, GameRules.DEFAULT)
            val gameID = gamesRepository.persist(newGame.toDTO())

            if (!lobbyRepository.completeLobby(lobbyDto.id, userID, gameID))
                throw InternalErrorAppException()

            LobbyInformation(lobbyDto.id, gameID)
        }
    }

    /**
     * Leaves the lobby
     * @param userID the user that is leaving the lobby
     * @param lobbyID the id of the lobby
     */
    fun leaveLobby(lobbyID: ID, userID: UserID) =
        transactionFactory.execute {
            val lobby = lobbyRepository.get(lobbyID)
            if (userID !in listOf(lobby?.player1,lobby?.player2))
                throw ForbiddenAccessAppException("You can't leave a lobby that you are not part of")

            if(lobby?.gameID != null)
                throw InvalidRequestException("You can't leave a lobby that is already in a game")

            if(!lobbyRepository.removePlayerFromLobby(lobbyID, userID))
                throw InternalErrorAppException()
        }


    /**
     * Makes a set of shots to the board of the game with the given id
     *
     * @param userID the user that is making the shots
     * @param gameID the id of the game
     * @param shots the shots to be made
     */
    fun makeShots(userID: UserID, gameID: ID, shots: List<Square>) {
        val gameState= transactionFactory.execute {
            val currentGameState =
                gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            if (userID !in currentGameState.playerBoards.keys)
                throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)

            if (currentGameState.state == Game.State.CANCELLED) {
                throw TimeoutExceededAppException(THIS_GAME_HAS_BEEN_CANCELLED)
            }

            if (userID != currentGameState.turnID)
                throw InvalidRequestException("Not your turn!")

            val newGameState = currentGameState.makePlay(shots)
            gamesRepository.persist(newGameState.toDTO())

            newGameState.state
        }
        if (gameState == Game.State.CANCELLED) {
            throw TimeoutExceededAppException(TOOK_TOO_LONG_MAKING_SHOTS)
        }
    }

    /**
     * Places a fleet in the board of the game with the given id
     *
     * @param userID the user that is placing the fleet
     * @param gameID the id of the game
     * @param ships the fleet to be placed
     * @throws GameNotFoundException if the game with the given id does not exist
     * @throws ForbiddenAccessAppException if the user is not in the game
     */
    fun defineFleetLayout(userID: UserID, gameID: ID, ships: List<ShipInfo>) {
        val gameState = transactionFactory.execute {
            val currentGameState = gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            if (userID !in currentGameState.playerBoards.keys)
                throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)

            if (currentGameState.state == Game.State.CANCELLED) {
                throw TimeoutExceededAppException(THIS_GAME_HAS_BEEN_CANCELLED)
            }

            val newGameState = currentGameState.placeShips(ships, userID)
            gamesRepository.persist(newGameState.toDTO())

            newGameState.state
        }

        if (gameState == Game.State.CANCELLED) {
            throw TimeoutExceededAppException(TOOK_TOO_LONG_PLACING_SHIPS)
        }
    }

    enum class Fleet { MY, OPPONENT }

    /**
     * Gets the fleet state of a user in a game
     * @param userID the user that is getting the fleet state
     * @param gameID the id of the game
     * @param whichFleet the fleet to be retrieved
     * @return [BoardDTO]
     */
    fun getFleetState(userID: UserID, gameID: ID, whichFleet: String): BoardDTO {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            game.playerBoards[userID] ?: throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)

            val fleetState = when (whichFleet) {
                "my" -> Fleet.MY
                "opponent" -> Fleet.OPPONENT
                else -> throw NotFoundAppException("Fleet $whichFleet not found")
            }

            val fleetOwnerID = when (fleetState) {
                Fleet.MY -> userID
                Fleet.OPPONENT -> game.playerBoards.keys.first { it != userID }
            }

            val board = game.playerBoards[fleetOwnerID] ?: throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)
            return@execute board.toDTO(fleetOwnerID, fleetState)
        }
    }

    /**
     * Gets the lobby state of the lobby identified by the given [lobbyID]
     * @param userID the user that is getting the lobby state
     * @param lobbyID the [ID] of the lobby
     *
     */
    fun getMyLobbyState(userID: UserID, lobbyID: ID): LobbyInformation {
        return transactionFactory.execute {
            val lobbyDto = lobbyRepository.get(lobbyID) ?: throw NotFoundAppException("Lobby $lobbyID")
            if(userID !in listOf(lobbyDto.player1, lobbyDto.player2))
                throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)

            LobbyInformation(
                lobbyID,
                lobbyDto.gameID
            )
        }
    }

    fun getGameRules(gameID: ID, userID: UserID) : GameRulesDTO {
        return transactionFactory.execute {
            val game = gamesRepository.get(gameID) ?: throw GameNotFoundException(gameID)
            if(userID !in game.playerBoards.keys)
                throw ForbiddenAccessAppException(MUST_BE_PARTICIPANT)
            if(game.state == Game.State.CANCELLED ){
                throw TimeoutExceededAppException(THIS_GAME_HAS_BEEN_CANCELLED)
            }
            game.rules.toDTO()
        }
    }

    /**
     * Gets all games where the user is playing in and the game is not finished nor cancelled
     * @param userID the user that is getting the games
     */
    fun geUserGames(userID: UserID, embedded: Boolean = false): EmbeddableGameListDTO {
        return transactionFactory.execute {
            val gameIDs = gamesRepository.getUserGames(userID)

            val gameStates = gameIDs
                .map { this@GameService.getGameState(it, userID).stateInfo }
                .takeIf { embedded }

            return@execute EmbeddableGameListDTO(GameListDTO(gameIDs), gameStates)
        }
    }

}

