package pt.isel.daw.battleship.services

import pt.isel.daw.battleship.services.exception.InvalidParameterException
import pt.isel.daw.battleship.services.exception.MissingParameterException
import java.security.MessageDigest

private val digest = MessageDigest.getInstance("SHA-512") // "SHA-512"

fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

fun hashPassword(password: String): String = digest.digest(password.toByteArray()).toHex()

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @return the parameter if it is not null, otherwise throw an exception.
 * @throws [InvalidParameterException] if the parameter is blank.
 */
fun requireNotBlankParameter(parameter: String?, parameterName: String): String? {
    if (parameter != null && parameter.isBlank()) throw InvalidParameterException(parameterName)
    return parameter
}

/**
 * @param parameter the parameter to check.
 * @param parameterName the name of the parameter to show on the Exception.
 *
 * @throws [MissingParameterException] if the parameter is null.
 * @throws [InvalidParameterException] if the parameter is blank.
 */
fun requireParameter(parameter: String?, parameterName: String): String {
    if (parameter == null) throw MissingParameterException(parameterName)
    requireNotBlankParameter(parameter, parameterName)
    return parameter
}

fun requireParameter( value : Boolean , message: () -> Any){
    if(!value) throw InvalidParameterException(message().toString())
}