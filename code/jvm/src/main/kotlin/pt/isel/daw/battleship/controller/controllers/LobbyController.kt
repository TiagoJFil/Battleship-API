package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Method.*
import pt.isel.daw.battleship.controller.MethodInfo
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren

import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
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
    }

    @Authentication
    @PostMapping(Uris.Lobby.CANCEL_QUEUE)
    fun cancelQueue(userID: UserID) : SirenEntity<Nothing> {
        gameService.leaveLobby(userID)
        return noEntitySiren(
            MethodInfo(Uris.Lobby.CANCEL_QUEUE, POST),
            mapOf("gameId" to "null")
        )
    }

}