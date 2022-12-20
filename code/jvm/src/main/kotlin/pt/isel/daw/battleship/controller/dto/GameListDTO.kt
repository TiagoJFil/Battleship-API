package pt.isel.daw.battleship.controller.dto

import pt.isel.daw.battleship.services.entities.GameStateInfo
import pt.isel.daw.battleship.utils.ID

data class GameListDTO(
    val values : List<ID>
)

data class EmbeddableGameListDTO(
    val gameList : GameListDTO,
    val gameStates : List<GameStateInfo>? = null
)