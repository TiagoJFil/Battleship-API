package pt.isel.daw.battleship.services.dto

import pt.isel.daw.battleship.model.Game
import pt.isel.daw.battleship.model.Id
import pt.isel.daw.battleship.services.entities.GameMapper

data class GameDBTO(
    val id: Id,
    val state: String,
    val winner: Id?,
    val player1: Id,
    val player2: Id,
){
    constructor(gameEntity: GameMapper): this(
        gameEntity.Id,
        gameEntity.state.toString(),
        gameEntity.winnerID,
        gameEntity.player1ID,
        gameEntity.player2ID,
    )

    fun toGame() = Game(id, Game.State.valueOf(state),)
}
