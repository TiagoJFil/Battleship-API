package pt.isel.daw.battleship.services.exception


/**
 * Base class for all exceptions related to the user.
 */
sealed class UserException(code: Int?, message: String?) : AppException(code, message)

/**
 * Thrown when a user with the given username already exists.
 */
class UserAlreadyExistsException(username: String) :
    UserException(ErrorCodes.User.ALREADY_EXISTS, "User $username already exists")


