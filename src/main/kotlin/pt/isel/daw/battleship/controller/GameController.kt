package pt.isel.daw.battleship.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.services.GameService


@RestController
@RequestMapping("game")
class GameController(
    val gameService: GameService
) {

    @PostMapping("")
    fun playIntent(@RequestBody playIntentInput: PlayIntentModelInput) {

    }


}