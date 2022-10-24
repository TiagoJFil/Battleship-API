package pt.isel.daw.battleship.services.exception

/**
 * Base class for all exceptions thrown by the application.
 */
sealed class AppException(val type: String?, message: String?) : Exception(message)

/**
 * Stores all the error codes.
 */
object ErrorTypes {

    object Game {
        const val NOT_FOUND = "https://battleship.com/problems/game-not-found"
    }

    object InputValidation {
        const val MISSING_PARAMETER = "https://battleship.com/problems/missing-parameter"
        const val INVALID_PARAMETER = "https://battleship.com/problems/invalid-parameter"
    }

    object User {
        const val ALREADY_EXISTS = "https://battleship.com/problems/user-already-exists"
        const val NOT_FOUND = "https://battleship.com/problems/user-not-found"
    }

    object General {
        const val UNAUTHORIZED = "https://battleship.com/problems/unauthorized"
        const val NOT_FOUND = "https://battleship.com/problems/not-found"
        const val INTERNAL_ERROR = "https://battleship.com/problems/internal-error"
        const val FORBIDDEN = "https://battleship.com/problems/unauthorized"
        const val METHOD_NOT_ALLOWED = "https://battleship.com/problems/method-not-allowed"
    }

}


