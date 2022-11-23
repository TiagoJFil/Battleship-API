package pt.isel.daw.battleship.controller.controllers

import noEntitySiren
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.AppSirenNavigation
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.appToSiren
import pt.isel.daw.battleship.controller.interceptors.authentication.Authentication
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.LobbyInformation
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID
import siren_navigation.builders.NoEntitySiren

@RestController
class LobbyController(
    val gameService: GameService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @Authentication
    @PostMapping(Uris.Lobby.QUEUE)
    fun playIntent(userID: UserID): SirenEntity<LobbyInformation> {
        val lobbyInfo = gameService.enqueue(userID)

        return lobbyInfo.appToSiren(AppSirenNavigation.LOBBY_STATE_NODE_KEY)
    }


    @Authentication
    @GetMapping(Uris.Lobby.STATE)
    fun getLobbyState(@PathVariable("lobbyId") lobbyId: ID, userID: UserID): SirenEntity<LobbyInformation> {
        val lobbyInfo: LobbyInformation = gameService.getMyLobbyState(userID, lobbyId)

        return lobbyInfo.appToSiren(AppSirenNavigation.LOBBY_STATE_NODE_KEY)
    }

    @Authentication
    @PostMapping(Uris.Lobby.CANCEL_QUEUE)
    fun cancelQueue(userID: UserID) : SirenEntity<NoEntitySiren> {
        gameService.leaveLobby(userID)
        return noEntitySiren(AppSirenNavigation.graph, AppSirenNavigation.USER_HOME_NODE_KEY)
    }

}