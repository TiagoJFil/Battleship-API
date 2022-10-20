package pt.isel.daw.battleship.services.exception

/**
 * Base class for all exceptions thrown by the application.
 */
sealed class AppException(val code: Int?, message: String?) : Exception(message)


/**
 * Stores all the error codes.
 */
object ErrorCodes {

    object InputValidation {
        const val MISSING_PARAMETER = 1000
        const val INVALID_PARAMETER = 1001
    }

    object User {
        const val ALREADY_EXISTS = 2000
        const val NOT_FOUND = 2001
    }

    object General {
        const val NOT_FOUND = 4000
    }

}



