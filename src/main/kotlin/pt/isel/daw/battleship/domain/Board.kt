package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.utils.ShipCount
import pt.isel.daw.battleship.utils.ShipSize
import java.util.*
import kotlin.math.sqrt

data class ShipInfo(val initialSquare: Square, val size : Int, val orientation : Orientation)

/**
 * Represents a battleship board.
 */
data class Board(val matrix: List<SquareType>) {

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

    fun indexToSquare(index: Int): Square {
        val rowOrdinal = index / side
        return Square(
            rowOrdinal = rowOrdinal, columnOrdinal = index - rowOrdinal * side
        )
    }

    /**
     * Gets the fleet composition of the Board
     */
    val fleetComposition by lazy<Map<ShipSize, ShipCount>> {
        val ships = mutableListOf<List<Square>>()
        val seen = mutableSetOf<Square>()
        matrix.forEachIndexed { idx, squareType ->
            val row = Row(idx / side)
            if(squareType == SquareType.ShipPart || squareType == SquareType.Hit){
                val square = Square(row, Column(idx - row.ordinal * side))
                if(square !in seen){
                    val shipParts = getShipParts(square)
                    ships.add(shipParts)
                    seen.addAll(shipParts)
                }
            }
        }

        ships.associate {
            val shipSize = it.size
            val shipCount = ships.count{ it.size == shipSize }
            shipSize to shipCount
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

    val side = sqrt(matrix.size.toDouble()).toInt()


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
     * @return List<Square> containing the diagonal neighbours of the ship
     */
    private fun Square.getDiagonals(): List<Square> {
        val topLDiagonal = Square((row.ordinal - 1), (column.ordinal - 1))
        val topRDiagonal = Square((row.ordinal - 1), (column.ordinal + 1))
        val bottomLDiagonal = Square((row.ordinal + 1), (column.ordinal - 1))
        val bottomRDiagonal = Square((row.ordinal + 1), (column.ordinal + 1))

        return listOfNotNull(
                topLDiagonal, topRDiagonal, bottomLDiagonal, bottomRDiagonal
        ).filter {
            getIndexFrom(it) in matrix.indices
        }
    }

    /**
     * Gets the neighbours of a square
     * @return List<Square> diagonal, vertical and horizontal neighbours of the ship
     */
    private fun Square.getNeighbours() : List<Square> {
        val axis = this.getAxisNeighbours()
        val diagonals = this.getDiagonals()
        return axis + diagonals
    }

    /**
     * Check around the given squares to prevent adjacent ships
     * @param shipSquares the ship parts
     * @throws IllegalArgumentException if there are adjacent ships
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
     * @return List<Square> containing the vertical and horizontal neighbours of the ship
     */
    private fun Square.getAxisNeighbours(): List<Square> {
        val top = Square((row.ordinal - 1), column.ordinal)
        val bottom = Square((row.ordinal + 1), column.ordinal)
        val left = Square(row.ordinal, (column.ordinal - 1))
        val right = Square(row.ordinal, (column.ordinal + 1))

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
     * @param initialSquare of the ship
     * @return [SearchResult]
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
     * Gets all the squares of a Ship
     * @param initialSquare of the ship
     * @return List<Square> containing all the ship parts
     */
    fun getShipParts(initialSquare: Square): List<Square> {
        val seen = mutableSetOf<Square>(initialSquare)
        val frontier = LinkedList<Square>()
        frontier.add(initialSquare)

        while (frontier.isNotEmpty()) {
            val square = frontier.removeFirst()
            val neighbours = square.getAxisNeighbours()
            neighbours.filter { sqr ->
                val squareType = matrix[getIndexFrom(sqr)]
                (squareType == SquareType.ShipPart || squareType == SquareType.Hit) && sqr !in seen
            }.forEach { sqr ->
                seen.add(sqr)
                frontier.add(sqr)
            }
        }
        return seen.toList()
    }

    /**
     * Gets the index from a given square in the Board
     * @param square square to check
     * @return [Int] the index of the given square
     */
    private fun getIndexFrom(square: Square): Int = square.row.ordinal * side + square.column.ordinal

    /**
     * Checks if the index from the given square is valid
     * @param square square to check
     * @return [Int] the index of the given square
     * @throws IllegalArgumentException if the square is not in the bounds of the board
     */
    private fun requireValidIndex(square: Square): Int {
        if( square.row.ordinal  > side || square.column.ordinal > side) {
            throw IllegalArgumentException("Square is out of bounds of the board.")
        }
        val index = getIndexFrom(square)
        require(index in matrix.indices) { "The index from the specified tile is not in the bounds of the board" }
        return index
    }

    /**
     * Returns true if the square trying to be shot has a [SquareType.ShipPart] in it
     * @param square square to check
     * @return [Boolean]
     */
    private fun isHit(square: Square) = get(square) == SquareType.ShipPart


    /**
     * Places a ship on the board given an initial square and final square.
     * @param initialSquare
     * @param finalSquare
     * @returns [Board] 
     * @throws IllegalArgumentException if the square is not in the bounds of the board
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

    /**
     * Checks if the given squares already have a ship part on it.a 
     * @param shipSquares
     * @throws IllegalArgumentException if the a ship part is already in a square
     */
    private fun checkShipSquares(shipSquares: List<Square>) {
        shipSquares.forEach {
            if (get(it) == SquareType.ShipPart) throw IllegalArgumentException("There is already a ship in this square.")
        }
    }

    /**
     * Places a ship on the board
     * @param initialSquare of the ship
     * @param shipSize
     * @param orientation Horizontal or Vertical
     * @returns [Board] the new board with the ship placed
     * @throws IllegalArgumentException if the square is not in the bounds of the board
     */
    fun placeShip(initialSquare: Square, shipSize: Int, orientation: Orientation): Board {
        requireValidIndex(initialSquare)

        val endSquare = if(orientation == Orientation.Horizontal){
            Square(initialSquare.row, (initialSquare.column.ordinal + shipSize - 1).column)
        } else {
            Square((initialSquare.row.ordinal + shipSize - 1).row, initialSquare.column)
        }

        requireValidIndex(endSquare)

        val shipSquaresIndexs = getShipSquares(initialSquare, endSquare)
                .map { square -> getIndexFrom(square) }

        return Board(
            matrix.mapIndexed { idx, squareType ->
                if (shipSquaresIndexs.contains(idx)) SquareType.ShipPart
                else squareType
            }
        )
    }

    /**
     * String representation of the board that can have the ships hidden
     * @param hiddenShips 
     * @return [String] representation of the board
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
     * @return [String] representation of the Board
     */
    override fun toString(): String {
        return toString(false)
    }
}

/**
 * Place a fleet on the Board
 * @param shipInfoList fleet info
 * @return [Board] board with the fleet on it
 */
fun Board.placeShipList(shipInfoList: List<Pair<Square, Square>>): Board =
        shipInfoList.fold(this){ acc, squares ->
            acc.placeShip(squares.first, squares.second)
        }

/**
 * Place a fleet on the Board
 * @param shipInfoList fleet info
 * @return [Board] board with the fleet on it
 */
fun Board.placeShips(shipInfoList : List<ShipInfo>) : Board =
        shipInfoList.fold(this){ acc, shipInfo ->
            acc.placeShip(shipInfo.initialSquare, shipInfo.size, shipInfo.orientation)
        }

/**
 * Make shots to the given squares
 * @param tiles to shot
 * @return [Board]
 */
fun Board.makeShots(tiles: List<Square>): Board =
    tiles.fold(this) { acc, square ->
        acc.shotTo(square)
    }

/**
 * Gets the squares that represent a ship
 * @param initialSquare of the ship
 * @param finalSquare of the ship
 * @return List<Square> the parts that represent the ship
 */
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

/**
 * Returns true if the board is in an end game state
 * @return [Boolean]
 */
fun Board.isInEndGameState() = matrix.none { it == Board.SquareType.ShipPart }

/**
 * Returns a board filled with water
 * @param boardSide 
 * @return Board
 */
fun Board.Companion.empty(boardSide: Int) = Board(
    List(boardSide * boardSide) { Board.SquareType.Water }
)

/**
 * Returns a String with a human-readable representation of the board
 * @return [String]
 */
fun Board.pretty() = toString().chunked(this.side).joinToString("\n")


