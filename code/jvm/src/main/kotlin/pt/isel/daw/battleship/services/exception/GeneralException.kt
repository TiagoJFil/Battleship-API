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
 * Indicates that the user is not authorized to perform the requested operation.
 */
class ForbiddenAccessAppException(message: String) :
    GeneralException(ErrorTypes.General.FORBIDDEN, message)

/**
 * Indicates that the user is not authenticated.
 */
class UnauthenticatedAppException() :
    GeneralException(ErrorTypes.General.UNAUTHORIZED, "Invalid or missing token")

/**
 * Indicates that the server did not receive a complete request message within the time that it was prepared to wait.
 */
class TimeoutExceededAppException(message : String) :
    GeneralException(ErrorTypes.General.TIMEOUT, message)