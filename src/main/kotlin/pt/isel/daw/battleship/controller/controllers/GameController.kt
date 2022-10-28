package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.toSiren
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.dto.input.LayoutInfoInputModel
import pt.isel.daw.battleship.controller.dto.input.ShotsInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.SirenLink
import pt.isel.daw.battleship.controller.hypermedia.selfLink
import pt.isel.daw.battleship.controller.interceptors.authentication.Authentication
import pt.isel.daw.battleship.repository.dto.BoardDTO
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.GameStateInfo
import pt.isel.daw.battleship.utils.UserID
import java.net.URI

@RestController
@RequestMapping(Uris.Game.ROOT)
class GameController(
    val gameService: GameService
) {

    @Authentication
    @GetMapping(Uris.Game.MY_FLEET)
    fun getUserFleet(@PathVariable("gameId") gameID: Int, userID: UserID): SirenEntity<BoardDTO> {
        val board = gameService.getFleet(userID, gameID, opponentFleet = false)

        board.toSiren(Uris.Game.MY_FLEET,mapOf("gameId" to gameID.toString()))

        return SirenEntity(
            properties = board,
            title = "UserFleet",
            entities = listOf(),
            actions = listOf(),
            links = listOf(
                selfLink("http://localhost:8080/api/game/$gameID/myFleet"),
                SirenLink(listOf("opponentFleet"), URI("http://localhost:8080/api/game/$gameID/opponentFleet"))
            )
        )
    }

    @Authentication
    @GetMapping(Uris.Game.OPPONENT_FLEET)
    fun getOpponentFleet(@PathVariable("gameId") gameID: Int, userID: UserID): SirenEntity<BoardDTO> {
        val board = gameService.getFleet(userID, gameID, opponentFleet = true)

        return SirenEntity(
            properties = board,
            title = "UserFleet",
            entities = listOf(),
            actions = listOf(),
            links = listOf(
                selfLink("http://localhost:8080/api/game/$gameID/opponentFleet"),
                SirenLink(listOf("opponentFleet"), URI("http://localhost:8080/api/game/$gameID/myFleet"))
            )
        )
    }

    @Authentication
    @PostMapping(Uris.Game.SHOTS_DEFINITION)
    fun defineShots(@PathVariable("gameId") gameID: Int, userID: UserID, @RequestBody input: ShotsInfoInputModel): SirenEntity<Nothing> {
        gameService.makeShots(userID, gameID, input.shots)

        return SirenEntity(
            properties = null,
            title = "Shots",
            entities = listOf(),
            actions = listOf(),
            links = listOf(
                selfLink("http://localhost:8080/api/game/$gameID/opponentFleet"),
                SirenLink(listOf("opponentFleet"), URI("http://localhost:8080/api/game/$gameID/myFleet"))
            )
        )
    }

    @Authentication
    @PostMapping(Uris.Game.LAYOUT_DEFINITION)
    fun defineLayout(@PathVariable("gameId") gameID: Int, userID: UserID, @RequestBody input: LayoutInfoInputModel): SirenEntity<Nothing> {
        gameService.defineFleetLayout(userID, gameID, input.shipsInfo)

        return SirenEntity(
            properties = null,
            title = "FleetLayout",
            entities = listOf(),
            actions = listOf(),
            links = listOf(

            )
        )
    }

    @Authentication
    @GetMapping(Uris.Game.GAME_STATE)
    fun getGameState(@PathVariable("gameId") gameID: Int, userID: UserID): SirenEntity<GameStateInfo> {
        val state = gameService.getGameState(gameID, userID)

        return SirenEntity(
            properties = GameStateInfo(state),
            title = "GameState",
            entities = listOf(),
            actions = listOf(),
            links = listOf(
            )
        )
    }



}