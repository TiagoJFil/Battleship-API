package pt.isel.daw.battleship.controller.pipeline.authentication

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.UserService
import pt.isel.daw.battleship.services.exception.UnauthenticatedAppException
import pt.isel.daw.battleship.utils.UserID
import javax.servlet.http.Cookie

/**
 * Represents the processor for the Authorization Cookie used in the authentication process
 */
@Component
class CookieAuthorizationProcessor(
    val userServices : UserService
) {

    fun process(cookies : Array<Cookie>?) : UserID {
        val authCookie = cookies?.find { it.name == COOKIE_AUTHORIZATION_NAME } ?: throw UnauthenticatedAppException()

        return userServices.getUserIDFromToken(authCookie.value)
    }
    companion object{
        const val COOKIE_AUTHORIZATION_NAME = "Authorization"
        const val COOKIE_USER_ID_NAME = "UID"
    }
}