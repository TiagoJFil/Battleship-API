package pt.isel.daw.battleship.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.api.interceptors.authentication.Authentication
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.utils.UserID

//pra authorization fazer um filter
@RestController
class GameController(
        private val gameService: GameService
) {

    companion object{
        private const val URI_PLAY_GAME = "/games/play"
    }

    @Authentication(
//        type = Authentication.Type.ID
    )
    @PostMapping(URI_PLAY_GAME)
    fun playGame(userID: UserID) : ResponseEntity<*> {
        TODO("Not yet implemented")
    }

}
