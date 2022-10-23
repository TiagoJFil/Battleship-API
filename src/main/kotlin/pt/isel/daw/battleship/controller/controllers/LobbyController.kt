package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*

import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.SirenAction
import pt.isel.daw.battleship.controller.hypermedia.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.selfLink
import pt.isel.daw.battleship.controller.interceptors.authentication.Authentication
import pt.isel.daw.battleship.model.Id
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.GameInformation
import pt.isel.daw.battleship.utils.UserID

@RestController
@RequestMapping(Uris.Lobby.ROOT)
class LobbyController(
    val gameService: GameService
) {

    @Authentication
    @PostMapping
    fun playIntent(userID: UserID): SirenEntity<GameInformation> {
        val gameId = gameService.createOrJoinLobby(userID)

        val placeShips = SirenAction(
            name = "place-ships",
            href = "http://localhost:8080/api/game/$gameId/place-ships",
            method = "POST",
            type = "application/json",
            fields = listOf(
            )
        )

        val home = SirenAction(
            name = "home",
            href = "http://localhost:8080/api",
            method = "GET",
            type = "application/json",
            fields = listOf(
            )
        )

        val actions: List<SirenAction>? = if (gameId != null) listOf(placeShips) else null

        return SirenEntity(
            properties = GameInformation(gameId),
            title = "Game",
            entities = listOf(),
            actions = actions,
            links = listOf(selfLink("http://localhost:8080/api/lobby"))
        )
    }

}