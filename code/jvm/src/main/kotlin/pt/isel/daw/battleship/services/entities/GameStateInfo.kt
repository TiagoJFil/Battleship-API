package pt.isel.daw.battleship.services.entities

import pt.isel.daw.battleship.domain.Game.*
import pt.isel.daw.battleship.utils.UserID

data class GameStateInfo(
    val state: State,
    val turnID : UserID,
    val player1ID : UserID,
    val player2ID : UserID
)

data class EmbeddableGameStateInfo(
    val stateInfo : GameStateInfo,
    val player1 : User?,
    val player2 : User?
)