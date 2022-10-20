package pt.isel.daw.battleship.services.exception


sealed class InputValidationException(code: Int, message: String) : AppException(code, message)


/**
 * Thrown when a required parameter is missing.
 */
class MissingParameterException(val parameter: String) :
    InputValidationException(ErrorCodes.InputValidation.MISSING_PARAMETER, "Missing parameter $parameter")


/**
 * Thrown when a parameter is invalid.
 */
class InvalidParameterException(val parameter: String) :
    InputValidationException(ErrorCodes.InputValidation.INVALID_PARAMETER, "Invalid parameter $parameter")