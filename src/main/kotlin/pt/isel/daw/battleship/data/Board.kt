

import kotlin.math.sqrt

class Board(private val board: List<Tile> ){

    enum class Tile(val representation: Char){
        ShipPart('B'),
        Shot('O'),
        Hit('X'),
        Water('#')
    }

    private val boardSize = sqrt(board.size.toDouble()).toInt()

    companion object{

        private val representationMap = Tile.values().associateBy { it.representation }

        fun fromLayout(layout: String): Board{
            val boardSide = sqrt(layout.length.toDouble())
            require(boardSide % 1.0 == 0.0){"Layout must represent a square"}

            val boardList = layout.chunked(boardSide.toInt())
                .flatMap { line ->
                    line.map {
                        representationMap[it] ?: throw IllegalArgumentException("Unknown Tile representation.")
                    }
                }

            return Board(boardList)

        }
    }

    /**
     * Gets a placeable from the board that is in the specified tile.
     * @param tile
     * @return Placeable
     */
    fun get(tile: Square): Tile = board[tile] ?: throw IllegalArgumentException("Tile out of bounds")

    /**
     * String representation of the Board
     */
    override fun toString(): String = this.board.joinToString(""){ it.representation }

}

fun getDefaultBoar