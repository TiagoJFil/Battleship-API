package pt.isel.daw.battleship.services.exception

/**
 * Base class for all exceptions related to the game.
 */
sealed class GameException(type: String?, message: String?) : AppException(type, message)

/**
 * Exception thrown when a game is not found.
 */
class GameNotFoundException(gameID: Int) :
    GameException(ErrorTypes.Game.NOT_FOUND, "Game with id $gameID not found")
