package pt.isel.daw.battleship.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.api.input.UserCreateInputModel
import pt.isel.daw.battleship.services.UserService


@RestController
class UserController (
    private val userService: UserService
){

    companion object{
        private const val URI_CREATE_USER = "/users/"
    }

    @PostMapping("/users")
    fun createUser(@RequestBody input : UserCreateInputModel) : ResponseEntity<*> {
        TODO("Not yet implemented")
      //  val user = userService.createUser(input.username, input.password)
      //  return ResponseEntity.status(HttpStatus.CREATED).body(user)
    }


}