package pt.isel.daw.battleship.controller.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.AppSirenNavigation
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.appToSiren
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.NoEntitySiren
import pt.isel.daw.battleship.controller.pipeline.authentication.Authentication
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.LobbyInformation
import pt.isel.daw.battleship.utils.ID
import pt.isel.daw.battleship.utils.UserID

@RestController
class LobbyController(
    val gameService: GameService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @Authentication
    @PostMapping(Uris.Lobby.QUEUE)
    fun playIntent(userID: UserID): SirenEntity<LobbyInformation> {
        val lobbyInfo = gameService.enqueue(userID)
        return lobbyInfo.appToSiren(AppSirenNavigation.PLAY_INTENT_NODE_KEY)
    }


    @Authentication
    @GetMapping(Uris.Lobby.STATE)
    fun getLobbyState(@PathVariable("lobbyId") lobbyId: ID, userID: UserID): SirenEntity<LobbyInformation> {
        val lobbyInfo: LobbyInformation = gameService.getMyLobbyState(userID, lobbyId)

        return lobbyInfo.appToSiren(AppSirenNavigation.LOBBY_STATE_NODE_KEY)
    }

    @Authentication
    @DeleteMapping(Uris.Lobby.CANCEL_QUEUE)
    fun cancelQueue(@PathVariable("lobbyId") lobbyId: ID, userID: UserID) : SirenEntity<NoEntitySiren> {
        gameService.leaveLobby(lobbyId, userID)
        return noEntitySiren(AppSirenNavigation.graph, AppSirenNavigation.USER_HOME_NODE_KEY)
    }

}