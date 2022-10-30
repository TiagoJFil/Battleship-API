package pt.isel.daw.battleship.services.exception


/**
 * Base class for all exceptions related to the user.
 */
sealed class UserException(type: String?, message: String?) : AppException(type, message)

/**
 * Thrown when a user with the given username already exists.
 */
class UserAlreadyExistsException(username: String) :
    UserException(ErrorTypes.User.ALREADY_EXISTS, "User $username already exists")

/**
 * Thrown when a user with the given username does not exist.
 */
class UserNotFoundException(username: String) :
    UserException(ErrorTypes.User.NOT_FOUND, "User $username does not exist")


