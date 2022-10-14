package pt.isel.daw.battleship.services.entities


import pt.isel.daw.battleship.model.Game
import pt.isel.daw.battleship.model.Id

data class GameMapper(
    val Id: Id,
    val state: Game.State,
    val winnerID: Id? = null,
    val player1ID: Id,
    val player2ID: Id,
)