package pt.isel.daw.battleship.controller.interceptors.authentication

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.UserService
import pt.isel.daw.battleship.utils.UserID

@Component
class AuthorizationHeaderProcessor(
    val userServices : UserService
) {

    fun process(token : String?) : UserID? {
        val parsedToken = token
            ?.substringAfter("Bearer ", missingDelimiterValue = "") ?: return null

        return userServices.getUserIDFromToken(parsedToken).getOrNull()
    }


    companion object {
        const val SCHEME: String = "Bearer"
    }

}