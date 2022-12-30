package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.*
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.dto.BoardDTO
import pt.isel.daw.battleship.controller.dto.GameListDTO
import pt.isel.daw.battleship.controller.dto.input.LayoutInfoInputModel
import pt.isel.daw.battleship.controller.dto.input.ShotsInfoInputModel
import pt.isel.daw.battleship.controller.hypermedia.siren.*
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.NoEntitySiren
import pt.isel.daw.battleship.controller.pipeline.authentication.Authentication
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.services.entities.GameRulesDTO
import pt.isel.daw.battleship.services.entities.GameStateInfo
import pt.isel.daw.battleship.utils.UserID

@RestController
class GameController(
    val gameService: GameService
) {

    @Authentication
    @GetMapping(Uris.Game.FLEET)
    fun getFleetState(
        @PathVariable("gameID") gameID: Int,
        userID: UserID,
        @PathVariable("whichFleet") fleet: String
    ): SirenEntity<BoardDTO> {
        val board = gameService.getFleetState(userID, gameID, whichFleet = fleet)
        return board.appToSiren(
            AppSirenNavigation.FLEET_NODE_KEY,
            mapOf("gameID" to gameID.toString(),"whichFleet" to fleet)
        )
    }

    @Authentication
    @PostMapping(Uris.Game.SHOTS_DEFINITION)
    fun defineShots(
        @RequestParam(required = false) embedded : Boolean,
        @PathVariable("gameID") gameID: Int,
        userID: UserID,
        @RequestBody input: ShotsInfoInputModel
    ): SirenEntity<NoEntitySiren> {
        gameService.makeShots(userID, gameID, input.shots)

        val siren = noEntitySiren(
            AppSirenNavigation.graph,
            AppSirenNavigation.SHOTS_DEFINITION_NODE_KEY,
            mapOf("gameID" to gameID.toString())
        )

        return if(embedded){
            val embeddableBoard = gameService.getFleetState(userID, gameID, whichFleet = "opponent")

            siren.appAppendEmbedded(
                AppSirenNavigation.SHOTS_DEFINITION_NODE_KEY,
                embeddableBoard,
                AppSirenNavigation.FLEET_NODE_KEY
            )
        }else{
            siren
        }
    }

    @Authentication
    @PostMapping(Uris.Game.LAYOUT_DEFINITION)
    fun defineLayout(
        @PathVariable("gameID") gameID: Int,
        userID: UserID,
        @RequestBody input: LayoutInfoInputModel
    ): SirenEntity<NoEntitySiren> {
        gameService.defineFleetLayout(userID, gameID, input.shipsInfo)

        return noEntitySiren(
            AppSirenNavigation.graph,
            AppSirenNavigation.DEFINE_LAYOUT_NODE_ID,
            mapOf("gameID" to gameID.toString())
        )
    }

    @Authentication
    @GetMapping(Uris.Game.STATE)
    fun getGameState(
        @PathVariable("gameID") gameID: Int,
        @RequestParam(required = false) embedded : Boolean,
        userID: UserID
    ): SirenEntity<GameStateInfo> {
        val embeddableGameStateInfo = gameService.getGameState(gameID, userID,embedded)

        val siren = embeddableGameStateInfo.stateInfo.appToSiren(
            AppSirenNavigation.GAME_STATE_NODE_KEY,
            mapOf("gameID" to gameID.toString())
        )

        return if(embedded) {
            val player1 = requireNotNull(embeddableGameStateInfo.player1)
            val player2 = requireNotNull(embeddableGameStateInfo.player2)

            return siren.appAppendEmbedded(
                AppSirenNavigation.GAME_STATE_NODE_KEY,
                player1,
                AppSirenNavigation.USER_NODE_KEY,
                mapOf("userID" to embeddableGameStateInfo.stateInfo.player1ID.toString())
            ).appAppendEmbedded(
                AppSirenNavigation.GAME_STATE_NODE_KEY,
                player2,
                AppSirenNavigation.USER_NODE_KEY,
                mapOf("userID" to embeddableGameStateInfo.stateInfo.player2ID.toString())
            )
        }else{
            siren
        }
    }

    @Authentication
    @GetMapping(Uris.Game.RULES)
    fun getGameRules(@PathVariable("gameID") gameID: Int, userID: UserID): SirenEntity<GameRulesDTO> {
        val rules = gameService.getGameRules(gameID, userID)
        return rules.appToSiren(
            AppSirenNavigation.GAME_RULES_NODE_KEY,
            mapOf("gameID" to gameID.toString())
        )
    }

    @Authentication
    @GetMapping(Uris.User.GAMES)
    fun getMyGames(
        @RequestParam(required = false) embedded : Boolean,
        userID: UserID
    ): SirenEntity<GameListDTO> {
        val games = gameService.geUserGames(userID,embedded)

        val siren = games.gameList.appToSiren(AppSirenNavigation.USER_GAMES_NODE_KEY)
        if(embedded){
            val gamesStates = requireNotNull(games.gameStates)
            return gamesStates.foldIndexed(siren){ index, acc, state ->
                acc.appAppendEmbedded(
                    AppSirenNavigation.USER_GAMES_NODE_KEY,
                    state,
                    AppSirenNavigation.GAME_STATE_NODE_KEY,
                    mapOf("gameID" to games.gameList.values[index].toString())
                )
            }
        }

        return siren
    }

}