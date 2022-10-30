package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Method.*
import pt.isel.daw.battleship.controller.MethodInfo
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren

import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.interceptors.authentication.Authentication
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.GameInformation
import pt.isel.daw.battleship.utils.UserID

@RestController
class LobbyController(
    val gameService: GameService
) {

    @Authentication
    @PostMapping(Uris.Lobby.QUEUE)
    fun playIntent(userID: UserID): SirenEntity<GameInformation> {
        val gameId = gameService.createOrJoinGame(userID)

        return GameInformation(gameId).toSiren(
            MethodInfo(Uris.Lobby.QUEUE, POST),
            mapOf("gameId" to gameId.toString())
        )
/*


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
 */
    }

    @Authentication
    @PostMapping(Uris.Lobby.CANCEL_QUEUE)
    fun cancelQueue(userID: UserID) : SirenEntity<Nothing?> {
        gameService.leaveLobby(userID)
        return null.toSiren(
            MethodInfo(Uris.Lobby.CANCEL_QUEUE, POST),
            mapOf("gameId" to "null")
        )
/*
        val playIntent = SirenAction(
            name = "play-intent",
            href = "http://localhost:8080/api/lobby/",
            method = "POST",
            type = "application/json",
            fields = listOf()
        )
        val home = SirenAction(
            name = "home",
            href = "http://localhost:8080/api",
            method = "GET",
            fields = listOf(
            )
        )
        use tosiren() but with Nothing

        return SirenEntity<Nothing>(
            title = "Home",
            entities = listOf(),
            actions = listOf(
                playIntent,
                home
            ),
            links = listOf(
                selfLink("http://localhost:8080/api/lobby")
            )
        )
 */
    }

}