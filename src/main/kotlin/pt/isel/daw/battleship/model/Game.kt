package pt.isel.daw.battleship.model

import pt.isel.daw.battleship.utils.ShipSize
import pt.isel.daw.battleship.utils.UserID

data class Game(
        val Id: Id,
        val state: State,
        val rules: GameRules = GameRules.DEFAULT,
        val boards: Map<UserID, Board>,
        val turn: UserID
){
    val turnBoard = boards[turn]
    val oppositeTurnIdx = 1 - turn
    val oppositeTurnBoard = boards[oppositeTurnIdx]
    val turnPlayer  = turn
    val oppositeTurnPlayer = oppositeTurnIdx

    enum class State {
        WAITING_PLAYER,
        PLACING_SHIPS,
        PLAYING,
        FINISHED
    }
}

/**
 * Returns a new game after a shot is made on the specified [Square]
 *
 * @param squares the squares to shot on
 * @throws IllegalArgumentException if the square is invalid according to the [Game.rules]
 */
fun Game.makeShot(squares: List<Square>): Game {
    if(squares.size != rules.shotsPerTurn) throw IllegalArgumentException("Invalid number of shots")
    val currentBoard = this.turnBoard ?: throw IllegalStateException("Board not initialized")
    val newBoard = currentBoard.makeShots(squares)
    return this.replaceBoard(oppositeTurnIdx, newBoard)
}

/**
 * Returns a new game after placing the ships on the board
 *
 * @throws IllegalArgumentException if the ship is invalid according to the [Game.rules]
 */
fun Game.placeShips(shipList: List<ShipInfo>) : Game {
    val currentBoard = this.turnBoard ?: throw IllegalStateException("Board not initialized")
    val newBoard =  currentBoard.placeShips(shipList)
    return this.replaceBoard(turn,newBoard)
}

/**
 * Returns a new Game after the board is changed
 */
fun Game.replaceBoard(turn: UserID, newBoard: Board): Game{
    val newBoards = boards.values.map {
        if(boards[turn] == it) newBoard
        else it
    }

    return this.copy(
        boards = boards.keys.associateWith { newBoards[it] }
    )
}

/**
 * Returns the next turn index
 */
fun Game.nextTurn(currentUser: UserID): Int = if(currentUser == turn) 1 else 0

private fun Int.verifyShipSize(size: Int?) {
    size ?: throw IllegalArgumentException("Ship is not accepted with the current game rules")
    if(this != size) throw IllegalArgumentException("Invalid ship size")
}

data class ShipRules(
    val fleetComposition: Map<ShipSize, Int>
)

data class GameRules(
    val shotsPerTurn: Int,
    val boardSide: Int,
    val maxTimeToPlay : Int,
    val maxTimeToDefineLayout : Int,
    val shipRules : ShipRules
    //fleet composition

) {
    companion object {
        val DEFAULT = GameRules(
            1,
            10,
            60,
            60,
            ShipRules(
                mapOf<ShipSize, Int>(
                    5 to 1,
                    4 to 1,
                    3 to 1,
                    2 to 1
                )
            )
        )
    }
}


