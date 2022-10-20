package pt.isel.daw.battleship.services.exception


/**
 * Base class for the multipurpose exceptions that are thrown by the application.
 */
sealed class GeneralException(code: Int, message: String) : AppException(code, message)

/**
 * Indicates that the requested resource was not found.
 */
class NotFoundAppException(resourceName: String) :
    GeneralException(ErrorCodes.General.NOT_FOUND, "The requested resource '$resourceName' was not found.")

