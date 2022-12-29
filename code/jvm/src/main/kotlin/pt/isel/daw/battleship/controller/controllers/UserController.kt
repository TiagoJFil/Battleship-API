package pt.isel.daw.battleship.controller.controllers


import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.dto.input.UserInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.siren.AppSirenNavigation
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.appToSiren
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.NoEntitySiren
import pt.isel.daw.battleship.controller.toCookies
import pt.isel.daw.battleship.services.UserService
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.entities.User
import pt.isel.daw.battleship.services.validationEntities.UserValidation
import pt.isel.daw.battleship.utils.UserID
import javax.servlet.http.HttpServletResponse


@RestController
class UserController(
    private val userService: UserService
) {
    companion object {
        const val COOKIE_LIFETIME = 60 * 60 * 24 * 7
        const val COOKIE_USER_ID_NAME = "UID"
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(Uris.User.REGISTER)
    fun createUser(@RequestBody input: UserInfoInputModel, response : HttpServletResponse): SirenEntity<AuthInformation> {
        val authInfo = userService.createUser(
            UserValidation(input.username, input.password)
        )
        authInfo.toCookies().forEach { response.addCookie(it) }

        return authInfo.appToSiren(AppSirenNavigation.REGISTER_NODE_KEY)
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(Uris.User.GET_USER)
    fun getUser(@PathVariable userID: UserID): SirenEntity<User> {
        val user = userService.getUser(userID)
        return user.appToSiren(
            AppSirenNavigation.USER_NODE_KEY,
            mapOf("userID" to userID.toString())
        )
    }

    @PostMapping(Uris.User.LOGIN)
    @ResponseStatus(HttpStatus.OK)
    fun authenticate(@RequestBody input: UserInfoInputModel, response : HttpServletResponse): SirenEntity<AuthInformation> {
        val authInfo = userService.authenticate(
            UserValidation(input.username, input.password)
        )
        authInfo.toCookies().forEach { response.addCookie(it) }

        return authInfo.appToSiren(AppSirenNavigation.LOGIN_NODE_KEY)
    }

    @GetMapping(Uris.User.HOME)
    fun getUserHome(): SirenEntity<NoEntitySiren>
        = noEntitySiren(AppSirenNavigation.graph, AppSirenNavigation.USER_HOME_NODE_KEY)

}