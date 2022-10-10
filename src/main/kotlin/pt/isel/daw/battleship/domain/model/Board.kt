package pt.isel.daw.battleship.domain.model

import pt.isel.daw.battleship.domain.*
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

data class ShipInfo(val initialSquare: Square, val ship : Game.Ship, val orientation : Orientation)

//TODO: ver em relaçao a dar display da board ao user
data class Board(val matrix: List<SquareType>) {

    val boardSide : Int =  sqrt(matrix.size.toDouble()).toInt()


    companion object {

        private val representationMap = SquareType.values().associateBy { it.representation }

        fun fromLayout(layout: String): Board {
            require(layout.isNotBlank()) { "Layout must not be blank. " }
            val boardSide = sqrt(layout.length.toDouble())
            require(boardSide % 1.0 == 0.0) { "Layout must represent a square." }

            return Board(
                layout.map {
                    representationMap[it] ?: throw IllegalArgumentException("Unknown Tile representation.")
                }
            )
        }

    }

    sealed class SearchResult

    object ClearDiagonals : SearchResult()
    data class ClearShipNeighbours(val shipSquares: List<Square>) : SearchResult()

    enum class SquareType(val representation: Char) {
        ShipPart('B'),
        Shot('O'),
        Hit('X'),
        Water('#');

        override fun toString(): String = representation.toString()

    }


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
        val shotSquareIdx = requireValidIndex(square)
        val isHit = isHit(square)
        val searchResult = if (isHit) searchKnownWaterSquares(square) else null
        if(matrix[shotSquareIdx] == SquareType.Hit
                ||
           matrix[shotSquareIdx] == SquareType.Shot) throw IllegalArgumentException("Square already shot")

        val squares = when (searchResult) {
            is ClearShipNeighbours ->
                searchResult.shipSquares
                    .flatMap { it.getAxisNeighbours() + it.getDiagonals() }
                    .distinct()
                    .filter { get(it) == SquareType.Water }
            is ClearDiagonals -> square.getDiagonals()
            else -> emptyList()
        }

        val knownWaterSquaresIdx = squares.map { getIndexFrom(it) }.toSet()

        val newBoardList = matrix.mapIndexed { idx, squareType ->
            if (idx != shotSquareIdx && idx !in knownWaterSquaresIdx) return@mapIndexed squareType

            if (isHit && idx !in knownWaterSquaresIdx)
                SquareType.Hit
            else
                SquareType.Shot
        }
        return Board(newBoardList)
    }

    /**
     * Gets the diagonal neighbours of a square
     */
    private fun Square.getDiagonals(): List<Square> {
        val topLDiagonal = SquareOrNull((row.ordinal - 1), (column.ordinal - 1))
        val topRDiagonal = SquareOrNull((row.ordinal - 1), (column.ordinal + 1))
        val bottomLDiagonal = SquareOrNull((row.ordinal + 1), (column.ordinal - 1))
        val bottomRDiagonal = SquareOrNull((row.ordinal + 1), (column.ordinal + 1))

        return listOfNotNull(
                topLDiagonal, topRDiagonal, bottomLDiagonal, bottomRDiagonal
        ).filter {
            getIndexFrom(it) in matrix.indices
        }
    }

    /**
     * Gets the neighbours of a square
     */
    private fun Square.getNeighbours() : List<Square> {
        val axis = this.getAxisNeighbours()
        val diagonals = this.getDiagonals()
        return axis + diagonals
    }

    /**
     * Check around the given squares
     */
    private fun checkForAdjacentShips(shipSquares: List<Square>) {
        val seen = mutableListOf<Square>()
        val unchecked = LinkedList(shipSquares)
        shipSquares.forEach { seen.add(it) }


        while (unchecked.isNotEmpty()) {
            val square = unchecked.removeFirst()
            val neighbours = square.getNeighbours()
            neighbours.forEach {
                if (it !in seen) {
                    if (get(it) == SquareType.ShipPart) {
                        throw IllegalArgumentException("Ships cannot be adjacent.")
                    }
                    seen.add(it)

                }
            }
        }

    }

    /**
     * Gets the neighbours of a square on the y axis and x axis
     */
    private fun Square.getAxisNeighbours(): List<Square> {
        val top = SquareOrNull((row.ordinal - 1), column.ordinal)
        val bottom = SquareOrNull((row.ordinal + 1), column.ordinal)
        val left = SquareOrNull(row.ordinal, (column.ordinal - 1))
        val right = SquareOrNull(row.ordinal, (column.ordinal + 1))

        return listOfNotNull(
            top, bottom, left, right
        ).filter {
            getIndexFrom(it) in matrix.indices
        }
    }

    /**
     * Searches for the known water squares after a hit and returns a result [SearchResult] that shows its format:
     * - All around
     * - Diagonal
     */
    fun searchKnownWaterSquares(initialSquare: Square): SearchResult {
        val seen = mutableSetOf<Square>()
        val frontier = LinkedList<Square>()

        frontier.add(initialSquare)

        while (frontier.isNotEmpty()) {
            val square = frontier.removeFirst()
            val neighbours = square.getAxisNeighbours()

            if (neighbours.any { get(it) == SquareType.ShipPart && it !in seen }) return ClearDiagonals

            val hits = neighbours.filter { get(it) == SquareType.Hit && it !in seen }
            seen.add(square)
            frontier.addAll(hits)
        }

        return ClearShipNeighbours(seen.toList())
    }

    /**
     * Gets the index from a given square in the Board
     */
    private fun getIndexFrom(square: Square): Int = square.row.ordinal * boardSide + square.column.ordinal

    /**
     * Gets the square from a given index in the Board
     */
    private fun getSquareFrom(index: Int): Square {
        val row = index / boardSide
        val column = index % boardSide
        return Square(row.row, column.column)
    }

    /**
     * Checks if the index from the given square is valid
     */
    private fun requireValidIndex(square: Square): Int {
        if( square.row.ordinal  > boardSide || square.column.ordinal > boardSide) {
            throw IllegalArgumentException("Square is out of bounds of the board.")
        }
        val index = getIndexFrom(square)
        require(index in matrix.indices) { "The index from the specified tile is not in the bounds of the board" }
        return index
    }

    /**
     * Returns true if the square trying to be shot has a [SquareType.ShipPart] in it
     */
    private fun isHit(square: Square) = get(square) == SquareType.ShipPart


    /**
     * Places a ship on the board given an initial square and final square
     */
    fun placeShip(initialSquare: Square, finalSquare: Square): Board {
        requireValidIndex(initialSquare)
        requireValidIndex(finalSquare)

        val shipSquares = getShipSquares(initialSquare, finalSquare)
        //need to verify if there is a ship there
        checkShipSquares(shipSquares)
        checkForAdjacentShips(shipSquares)


        val shipSquaresIndexs = shipSquares.map { square -> getIndexFrom(square) }

        return Board(
            matrix.mapIndexed { idx, squareType ->
                if (shipSquaresIndexs.contains(idx)) SquareType.ShipPart
                else squareType
            }
        )

    }

    private fun checkShipSquares(shipSquares: List<Square>) {
        shipSquares.forEach {
            if (get(it) == SquareType.ShipPart ) throw IllegalArgumentException("There is already a ship in this square.")
        }
    }

    fun placeShip(initialSquare: Square, ship: Game.Ship, orientation: Orientation): Board{
        requireValidIndex(initialSquare)

        val shipSize = ship.size
        //get the endSquare
        val endSquare = if(orientation == Orientation.Horizontal){
            Square(initialSquare.row, (initialSquare.column.ordinal + shipSize - 1).column)
        } else {
            Square((initialSquare.row.ordinal + shipSize - 1).row, initialSquare.column)
        }
        return placeShip(initialSquare,endSquare)
    }


    /**
     * String representation of the board that can have the ships hidden
     */
     fun toString(hiddenShips : Boolean = false): String {
        val method: (SquareType) -> String =
            if (hiddenShips) {
                {
                    if (it == SquareType.ShipPart) SquareType.Water.toString()
                    else it.toString()
                }
            } else {
                { it.representation.toString() }
            }

        return matrix.joinToString("") { method(it) }
    }
    /**
     * String representation of the Board
     */
    override fun toString(): String {
        return toString(false)
    }

}


fun Board.placeShipList(shipInfoList: List<Pair<Square, Square>>): Board =
        shipInfoList.fold(this){ acc, pair ->
            acc.placeShip(pair.first,pair.second)
        }

fun Board.placeShips(shipInfoList : List<ShipInfo>) : Board =
        shipInfoList.fold(this){ acc, shipInfo ->
            acc.placeShip(shipInfo.initialSquare, shipInfo.ship, shipInfo.orientation)
        }

fun Board.makeShots(tiles: List<Square>): Board =
    tiles.fold(this) { acc, square ->
        acc.shotTo(square)
    }



private fun getShipSquares(initialSquare: Square, finalSquare: Square): List<Square> {
    val squareList = mutableListOf<Square>()

    val squaresVector = Vector(initialSquare, finalSquare)

    for (it in 0..squaresVector.absDirection) {
        squareList.add(
            if (squaresVector.orientation == Orientation.Horizontal)
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


    val orientation = Orientation.get(initialSquare, finalSquare)
        ?: throw IllegalArgumentException("The squares are not in the same row or column")

    val direction = if (orientation == Orientation.Horizontal)
        finalSquare.column - initialSquare.column
    else
        finalSquare.row - initialSquare.row

    val absDirection = abs(direction)

    val factor = if (direction > 0) 1 else -1
}

enum class Orientation {
    Horizontal,
    Vertical;

    companion object {
        fun get(initialSquare: Square, finalSquare: Square): Orientation? {
            val verticalSize = abs(initialSquare.row - finalSquare.row)
            val horizontalSize = abs(initialSquare.column - finalSquare.column)

            return if (verticalSize == 0) {
                Horizontal
            } else if (horizontalSize == 0) {
                Vertical
            } else {
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
    List(boardSide * boardSide) { Board.SquareType.Water }
)

/**
 * Returns a String with a human-readable representation of the board
 */
fun Board.pretty() = toString().chunked(this.boardSide).joinToString("\n")


