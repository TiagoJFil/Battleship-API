package pt.isel.daw.battleship.services

import pt.isel.daw.battleship.services.exception.InvalidParameterException
import pt.isel.daw.battleship.services.exception.MissingParameterException
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Duration
import java.util.*

private val digest = MessageDigest.getInstance("SHA-512") // "SHA-512"

/**
 * Converts the given [ByteArray] into a [String] with the hexadecimal representation.
 */
fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }

/**
 * Hashes the password using SHA-512 with the given salt.
 */
fun hashPassword(password: String, salt: String): String {
    val passPlusSalt = password + salt
    return digest.digest(passPlusSalt.toByteArray()).toHex()
}

/**
 * Generates a random UUID.
 */
fun generateUUID() = UUID.randomUUID().toString()

/**
 * Generates a random Salt for the password.
 */
fun generateSalt(): String {
    val random = SecureRandom()
    val salt = ByteArray(32)
    random.nextBytes(salt)
    return salt.toString()
}

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

/**
 * @param value to assert as true.
 * @param message to show on the exception.
 *
 * @throws [InvalidParameterException] if the value is false.
 */
fun requireParameter( value : Boolean , message: () -> Any){
    if(!value) throw InvalidParameterException(message().toString())
}

/**
 * @param minutes the time to be converted.
 * @return [Long] the time in milliseconds.
 */
fun minutesToMillis(minutes: Long) = Duration.ofMinutes(minutes).toMillis()

/**
 * @param seconds the time to be converted.
 * @return [Long] the time in milliseconds.
 */
fun secondsToMillis(seconds: Long) = Duration.ofSeconds(seconds).toMillis()