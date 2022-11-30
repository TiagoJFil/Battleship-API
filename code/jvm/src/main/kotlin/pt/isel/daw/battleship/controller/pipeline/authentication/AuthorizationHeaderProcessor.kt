package pt.isel.daw.battleship.controller.pipeline.authentication

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.UserService
import pt.isel.daw.battleship.services.exception.UnauthenticatedAppException
import pt.isel.daw.battleship.utils.UserID

/**
 * Represents the processor for the Authorization header used in the authentication process
 */
@Component
class AuthorizationHeaderProcessor(
    val userServices : UserService
) {

    fun process(token : String?) : UserID {
        val parsedToken = token
            ?.substringAfter("$SCHEME ", missingDelimiterValue = "") ?: throw UnauthenticatedAppException()

        return userServices.getUserIDFromToken(parsedToken)
    }


    companion object {
        const val SCHEME: String = "Bearer"
    }

}