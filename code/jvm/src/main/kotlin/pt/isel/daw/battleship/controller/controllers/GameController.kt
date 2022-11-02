package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.dto.input.LayoutInfoInputModel
import pt.isel.daw.battleship.controller.dto.input.ShotsInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren
import pt.isel.daw.battleship.controller.interceptors.authentication.Authentication
import pt.isel.daw.battleship.repository.dto.BoardDTO
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.GameStateInfo
import pt.isel.daw.battleship.utils.UserID

@RestController
class GameController(
    val gameService: GameService
) {

    @Authentication
    @GetMapping(Uris.Game.MY_FLEET)
    fun getUserFleet(@PathVariable("gameId") gameID: Int, userID: UserID): SirenEntity<BoardDTO> {
        val board = gameService.getFleetState(userID, gameID, whichFleet = GameService.Fleet.MY)
        return board.toSiren(
            AppEndpointsMetaData.myFleet,
            mapOf("gameId" to gameID.toString()),
        )
    }

    @Authentication
    @GetMapping(Uris.Game.OPPONENT_FLEET)
    fun getOpponentFleet(@PathVariable("gameId") gameID: Int, userID: UserID): SirenEntity<BoardDTO> {
        val board = gameService.getFleetState(userID, gameID, whichFleet = GameService.Fleet.OPPONENT)

        return board.toSiren(
            AppEndpointsMetaData.opponentFleet,
            mapOf("gameId" to gameID.toString())
        )
    }

    @Authentication
    @PostMapping(Uris.Game.SHOTS_DEFINITION)
    fun defineShots(@PathVariable("gameId") gameID: Int, userID: UserID, @RequestBody input: ShotsInfoInputModel): SirenEntity<Nothing> {
        gameService.makeShots(userID, gameID, input.shots)

       return noEntitySiren(
            AppEndpointsMetaData.shotsDefinition,
            mapOf("gameId" to gameID.toString())
        )
    }

    @Authentication
    @PostMapping(Uris.Game.LAYOUT_DEFINITION)
    fun defineLayout(@PathVariable("gameId") gameID: Int, userID: UserID, @RequestBody input: LayoutInfoInputModel): SirenEntity<Nothing> {
        gameService.defineFleetLayout(userID, gameID, input.shipsInfo)

        return noEntitySiren(
            AppEndpointsMetaData.layoutDefinition,
            mapOf("gameId" to gameID.toString())
        )
    }

    @Authentication
    @GetMapping(Uris.Game.GAME_STATE)
    fun getGameState(@PathVariable("gameId") gameID: Int, userID: UserID): SirenEntity<GameStateInfo> {
        val state = gameService.getGameState(gameID, userID)

        return state.toSiren(
            AppEndpointsMetaData.gameState,
            mapOf("gameId" to gameID.toString())
        )
    }
}