package pt.isel.daw.battleship.controller.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.controller.Method.*
import pt.isel.daw.battleship.controller.MethodInfo
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.dto.input.UserInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.services.UserService
import pt.isel.daw.battleship.services.entities.AuthInformation
import pt.isel.daw.battleship.services.validationEntities.UserValidation


@RestController
class UserController(
    private val userService: UserService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(Uris.User.REGISTER)
    fun createUser(@RequestBody input: UserInfoInputModel): SirenEntity<AuthInformation> {
        val authInfo = userService.createUser(
            UserValidation(input.username, input.password)
        )

        return authInfo.toSiren(MethodInfo(Uris.User.REGISTER, POST))
    }
    @PostMapping(Uris.User.LOGIN)
    @ResponseStatus(HttpStatus.OK)
    fun authenticate(@RequestBody input: UserInfoInputModel): SirenEntity<AuthInformation> {
        val authInfo = userService.authenticate(
            UserValidation(input.username, input.password)
        )

        return authInfo.toSiren(MethodInfo(Uris.User.LOGIN, POST))
    }

    @GetMapping(Uris.User.HOME)
    fun getUserHome(): SirenEntity<Nothing> = noEntitySiren(AppEndpointsMetaData.userRoot)

}