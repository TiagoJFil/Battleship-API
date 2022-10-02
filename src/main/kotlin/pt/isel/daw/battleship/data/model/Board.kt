package pt.isel.daw.battleship.data.model

import pt.isel.daw.battleship.data.Square
import kotlin.math.sqrt

data class Board(val matrix: List<SquareType>) {

    companion object {

        private val representationMap = SquareType.values().associateBy { it.representation }

        fun fromLayout(layout: String): Board {
            require(layout.isNotBlank()){ "Layout must not be blank. "}
            val boardSide = sqrt(layout.length.toDouble())
            require(boardSide % 1.0 == 0.0) { "Layout must represent a square." }

            return Board(
                layout.map {
                    representationMap[it] ?: throw IllegalArgumentException("Unknown Tile representation.")
                }
            )
        }

    }

    enum class SquareType(val representation: Char) {
        ShipPart('B'),
        Shot('O'),
        Hit('X'),
        Water('#')
    }

    val boardSide = sqrt(matrix.size.toDouble()).toInt()


    /**
     * Gets a placeable from the board that is in the specified tile.
     * @param square
     * @return Placeable
     * @throws IllegalArgumentException if the square is out of bounds of the board
     */
    operator fun get(square: Square): SquareType = matrix[requireValidIndex(square)]


    /**
     * Makes a shot to the specified square
     *
     * @param square
     * @return [Board]
     * @throws IllegalArgumentException if the square is out of bounds of the board
     */
    fun shotTo(square: Square): Board {
        val squareIndex = requireValidIndex(square)
        val newBoardList = matrix.mapIndexed { idx, squareType ->
            if(idx != squareIndex) return@mapIndexed squareType

            if(isHit(square))
                SquareType.Hit
            else
                SquareType.Shot

        }
        return Board(newBoardList)
    }

    /**
     * String representation of the Board
     */
    override fun toString(): String = this.matrix.joinToString("") { it.representation.toString() }


    /**
     * Gets the index from a given square in the Board
     */
    private fun getIndexFrom(square: Square): Int = square.row.ordinal * boardSide + square.column.ordinal

    /**
     * Checks if the index from the given square is valid
     */
    private fun requireValidIndex(square: Square): Int {
        val index = getIndexFrom(square)
        require(index in matrix.indices) { "The index from the specified tile is not in the bounds of the board" }
        return index
    }

    /**
     * Returns true if the square trying to be shot has a [SquareType.ShipPart] in it
     */
    private fun isHit(square: Square) = get(square) == SquareType.ShipPart

}

/**
 * Returns true if the board is in an end game state
 */
fun Board.isInEndGameState() = matrix.none { it == Board.SquareType.ShipPart }

/**
 * Returns a board filled with water
 */
fun Board.Companion.empty(boardSide: Int) = Board(
    List(boardSide * boardSide){ Board.SquareType.Water }
)

/**
 * Returns a String with a human-readable representation of the board
 */
fun Board.pretty() = toString().chunked(this.boardSide).joinToString("\n")


