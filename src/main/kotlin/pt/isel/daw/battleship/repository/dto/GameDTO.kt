package pt.isel.daw.battleship.repository.dto


import org.jdbi.v3.core.mapper.Nested
import pt.isel.daw.battleship.domain.Board
import pt.isel.daw.battleship.domain.Game
import pt.isel.daw.battleship.domain.GameRules
import pt.isel.daw.battleship.domain.Id
import pt.isel.daw.battleship.utils.UserID
import java.sql.Timestamp

data class GameDTO(
    val id: Id?,
    val state: String,
    @Nested val rules: GameRules,
    val turn: UserID,
    val player1: UserID?,
    val player2: UserID?,
    val boardP1: String?,
    val boardP2: String?,
    val lastUpdated : Timestamp
) {
    fun toGame() = Game(
        id= id,
        state= Game.State.valueOf(state.uppercase()),
        rules= rules,
        boards= if(player1 == null || player2 == null || boardP1 == null || boardP2 == null)
            emptyMap()
        else mapOf(
            player1 to Board.fromLayout(boardP1),
            player2 to Board.fromLayout(boardP2)
        ),
        turn,
        lastUpdated.time
    )
}


fun Game.toDTO() = GameDTO(
    id = id,
    state = state.toString().lowercase(),
    rules = rules,
    turn = turnID,
    player1 = boards.keys.firstOrNull(),
    player2 = boards.keys.lastOrNull(),
    boardP1 = boards.values.firstOrNull()?.toString(),
    boardP2 = boards.values.lastOrNull()?.toString(),
    lastUpdated = Timestamp(lastUpdated)
)









