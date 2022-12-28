package pt.isel.daw.battleship.services.exception


sealed class InputValidationException(type: String, message: String) : AppException(type, message)


/**
 * Thrown when a required parameter is missing.
 */
class MissingParameterException(parameter: String) :
    InputValidationException(ErrorTypes.InputValidation.MISSING_PARAMETER, "Missing parameter $parameter")


/**
 * Thrown when a parameter is invalid.
 */
class InvalidParameterException(parameter: String) :
    InputValidationException(ErrorTypes.InputValidation.INVALID_PARAMETER, "Invalid parameter $parameter")

