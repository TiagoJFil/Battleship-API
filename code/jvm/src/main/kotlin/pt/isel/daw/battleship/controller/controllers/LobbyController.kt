package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren

import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.controller.interceptors.authentication.Authentication
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.LobbyInformation
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID

@RestController
class LobbyController(
    val gameService: GameService
) {

    @Authentication
    @PostMapping(Uris.Lobby.QUEUE)
    fun playIntent(userID: UserID): SirenEntity<LobbyInformation> {
        val lobbyInfo = gameService.enqueue(userID)

        return lobbyInfo.toSiren(
            AppEndpointsMetaData.queue,
            mapOf(
                "lobbyId" to lobbyInfo.id.toString(),
                "gameId" to lobbyInfo.gameID?.toString()
            )
        )
    }

    @Authentication
    @GetMapping(Uris.Lobby.STATE)
    fun getLobbyState(@PathVariable("lobbyId") lobbyId: ID, userID: UserID): SirenEntity<LobbyInformation> {
        val lobbyInfo: LobbyInformation = gameService.getMyLobbyState(userID, lobbyId)

        return lobbyInfo.toSiren(
            AppEndpointsMetaData.lobbyState,
            mapOf("lobbyId" to lobbyId.toString())
        )
    }

    @Authentication
    @PostMapping(Uris.Lobby.CANCEL_QUEUE)
    fun cancelQueue(userID: UserID) : SirenEntity<Nothing> {
        gameService.leaveLobby(userID)
        return noEntitySiren(
            AppEndpointsMetaData.cancelQueue,
            mapOf("gameId" to "null")
        )
    }

}