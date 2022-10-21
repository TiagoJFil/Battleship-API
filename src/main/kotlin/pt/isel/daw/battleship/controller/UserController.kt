package pt.isel.daw.battleship.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.controller.dto.input.UserCreateInputModel
import pt.isel.daw.battleship.services.UserService
import pt.isel.daw.battleship.services.entities.UserInfo
import pt.isel.daw.battleship.services.validationEntities.UserCreation
import java.net.URI


@RestController(Uris.User.ROOT)
class UserController (
    private val userService: UserService
) {

    @PostMapping(Uris.User.REGISTER)
    fun createUser(@RequestBody input: UserCreateInputModel): UserInfo? {

        val result = userService.createUser(
            UserCreation(input.username, input.password)
        )

        result.onSuccess {
            return it
        }.onFailure {
            Problem.userAlreadyExists
        }

        return result.getOrNull()
    }

}