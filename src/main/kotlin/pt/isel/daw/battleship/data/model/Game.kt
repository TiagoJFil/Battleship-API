package pt.isel.daw.battleship.data.model

import pt.isel.daw.battleship.data.Id
import pt.isel.daw.battleship.data.Square
import pt.isel.daw.battleship.services.GameService

data class Player (val id : Id, val name : String )

data class Game(
        val Id: Id,
        val state: State,
        val rules: GameRules = GameRules.DEFAULT,
        val boards: List<Board> = List(2){ Board.empty(rules.boardSide) },
        val players : List<Player> = listOf( Player(1,"p1") , Player(2,"p2") ),
        val turnIdx: Int
){
    val turnBoard get() = boards[turnIdx]
    val oppositeTurnBoard get() = boards[1-turnIdx]
    val turnPlayer get() = players[turnIdx]
    val oppositeTurnPlayer get() = players[1-turnIdx]

    sealed class Ship(val size: Int)
    class Carrier(size: Int) : Ship(size)
    class Battleship(size: Int) : Ship(size)
    class Cruiser(size: Int) : Ship(size)
    class Submarine(size: Int) : Ship(size)
    class Destroyer(size: Int) : Ship(size)


    enum class State {
        WAITING_PLAYER,
        PLACING_SHIPS,
        PLAYING,
        FINISHED
    }

}

/**
 * Returns a new game after a shot is made on the specified [square]
 *
 * @throws IllegalArgumentException if the square is invalid according to the [Game.rules]
 */
fun Game.makeShot(square: Square): Game = this.copy(
        boards= boards.mapIndexed { idx, board ->
            if(idx == turnIdx) return@mapIndexed board

            board.shotTo(square)
        },
        turnIdx = this.nextTurn()
    )

/**
 * Returns the next turn index
 */
fun Game.nextTurn(): Int = if(turnIdx > 0) 0 else 1


private fun Int.verifyShipSize(size: Int?) {
    size ?: throw IllegalArgumentException("Ship does not exist")
    if(this != size) throw IllegalArgumentException("Invalid ship size")
}

data class GameRules(
    val shotsPerRound: Int,
    val boardSide: Int,
    val maxTimeToPlay : Int,
    val maxTimeToDefineLayout : Int,
    //fleet composition
    val carrierSize: Int?,
    val battleshipSize: Int?,
    val cruiserSize: Int?,
    val submarineSize: Int?,
    val destroyerSize: Int?
) {

    fun verifyShips(shipList: List<ShipInfo>) {
        shipList.forEach { shipInfo ->
            when (val ship = shipInfo.ship) {
                is Game.Carrier -> ship.size.verifyShipSize(carrierSize)
                is Game.Battleship -> ship.size.verifyShipSize(battleshipSize)
                is Game.Cruiser -> ship.size.verifyShipSize(cruiserSize)
                is Game.Submarine -> ship.size.verifyShipSize(submarineSize)
                is Game.Destroyer -> ship.size.verifyShipSize(destroyerSize)

            }
        }
    }

    companion object {
        val DEFAULT = GameRules(
                1, 10,60,
                60,5,4,
                3,3,2
        )
    }
}


