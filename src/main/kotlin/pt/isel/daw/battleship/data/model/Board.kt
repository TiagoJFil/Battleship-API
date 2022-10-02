package pt.isel.daw.battleship.data.model

import pt.isel.daw.battleship.data.Column
import pt.isel.daw.battleship.data.Row
import pt.isel.daw.battleship.data.Square
import kotlin.math.abs
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

    fun placeShip(initialSquare : Square,finalSquare: Square): Board {
        requireValidIndex(initialSquare)
        requireValidIndex(finalSquare)


        val shipSquaresIndexs = getShipSquares(initialSquare,finalSquare)
            .map { square -> getIndexFrom(square) }

        return Board(
            matrix.mapIndexed { idx, squareType ->
                if(shipSquaresIndexs.contains(idx)) SquareType.ShipPart
                else squareType
            }
        )

    }

}



private fun getShipSquares(initialSquare: Square, finalSquare: Square) : List<Square> {
    val squareList = mutableListOf<Square>()

    val squaresVector = Vector(initialSquare,finalSquare)

    for(it in 0..abs(squaresVector.direction)){
        squareList.add(
            if(squaresVector.orientation == Orientation.Horizontal)
                Square(
                    initialSquare.row,
                    Column(initialSquare.column.ordinal + it * squaresVector.factor)
                )
            else
                Square(
                    Row(initialSquare.row.ordinal + it * squaresVector.factor),
                    initialSquare.column
                )
        )
    }

    return squareList
}


class Vector(initialSquare: Square, finalSquare: Square) {


    val orientation = Orientation.get(initialSquare,finalSquare) ?: throw IllegalArgumentException("The squares are not in the same row or column")

    val direction = if(orientation == Orientation.Horizontal)
        finalSquare.column - initialSquare.column
    else
        finalSquare.row - initialSquare.row

    val absDirection = abs(direction)

    val factor = if(direction > 0) 1 else -1
}

enum class Orientation {
    Horizontal,
    Vertical;

    companion object{
        fun get( initialSquare: Square, finalSquare: Square ): Orientation?{
            val verticalSize = abs( initialSquare.row - finalSquare.row)
            val horizontalSize = abs( initialSquare.column -  finalSquare.column)

            return if(verticalSize == 0){
                Horizontal
            } else if(horizontalSize == 0){
                Vertical
            }else {
                null
            }
        }
    }

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


