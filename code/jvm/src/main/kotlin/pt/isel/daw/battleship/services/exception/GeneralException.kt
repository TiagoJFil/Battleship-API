package pt.isel.daw.battleship.services.exception


/**
 * Base class for the multipurpose exceptions that are thrown by the application.
 */
sealed class GeneralException(type: String, message: String) : AppException(type, message)

/**
 * Indicates that the requested resource was not found.
 */
class NotFoundAppException(resourceName: String) :
    GeneralException(ErrorTypes.General.NOT_FOUND, "The requested resource '$resourceName' was not found.")

/**
 * Indicates that there has occurred an error while processing the request.
 */
class InternalErrorAppException() :
    GeneralException(ErrorTypes.General.INTERNAL_ERROR, "An internal error has occurred while processing the request.")

/**
 * Thrown when the user makes an invalid request.
 */
class InvalidRequestException(message: String) :
    InputValidationException(ErrorTypes.General.INVALID_REQUEST, message)

/**
 * Indicates that the user is not authorized to perform the requested operation.
 *
 * Similar to UnauthorizedException, but this exception is thrown when the user is authenticated, but is not allowed to
 * perform the requested operation.
 * The access is permanently forbidden and is linked to the application logic (like an incorrect password)
 */
class ForbiddenAccessAppException(message: String) :
    GeneralException(ErrorTypes.General.FORBIDDEN, message)

/**
 * Indicates that the user is not authenticated.
 * Similar to ForbiddenException, but this exception is thrown when the user is not authenticated.
 * The user can authenticate to be able to perform the operation.
 */
class UnauthenticatedAppException() :
    GeneralException(ErrorTypes.General.UNAUTHORIZED, "Invalid or missing token")

/**
 * Indicates that the server did not receive a complete request message within the time that it was prepared to wait.
 */
class TimeoutExceededAppException(message : String) :
    GeneralException(ErrorTypes.General.TIMEOUT, message)