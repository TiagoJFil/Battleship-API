package pt.isel.daw.battleship.domain.board

import pt.isel.daw.battleship.domain.*
import pt.isel.daw.battleship.utils.ShipCount
import pt.isel.daw.battleship.utils.ShipSize
import java.util.*
import kotlin.math.sqrt

data class ShipInfo(val initialSquare: Square, val size : Int, val orientation : Orientation)

/**
 * Represents a battleship board.
 */
data class Board(val linearMatrix: List<SquareType>) {

    companion object {

        private val representationMap = SquareType.values().associateBy { it.representation }

        fun fromLayout(layout: String): Board {
            require(layout.isNotBlank()) { "Layout must not be blank. " }
            val boardSide = sqrt(layout.length.toDouble())
            val boardSideIsInteger = boardSide % 1.0 == 0.0
            require(boardSideIsInteger) { "Layout must represent a square." }

            return Board(
                layout.map {
                    representationMap[it] ?: throw IllegalArgumentException("Unknown Tile representation.")
                }
            )
        }

    }

    /**
     * Gets the fleet composition of the Board.
     * The fleet composition is an occurrence map of the ship sizes.
     *
     * @return the fleet composition
     * @see ShipSize
     * @see ShipCount
     */
    val fleetComposition: Map<ShipSize, ShipCount> by lazy {
        val ships = mutableListOf<List<Square>>()
        val seen = mutableSetOf<Square>()

        linearMatrix.forEachIndexed { idx, squareType ->
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

        ships.groupingBy { it.size }
            .eachCount()
    }

    enum class SquareType(val representation: Char) {
        ShipPart('B'),
        Shot('O'),
        Hit('X'),
        Water('#');


        override fun toString(): String = representation.toString()
    }

    /**
     * The side of the board.
     */
    val side = sqrt(linearMatrix.size.toDouble()).toInt()


    /**
     * Gets a placeable from the board that is in the specified tile.
     * @param square
     * @return Placeable
     * @throws IllegalArgumentException if the square is out of bounds of the board
     */
    operator fun get(square: Square): SquareType = linearMatrix[requireValidIndex(square)]


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
        val searchResult = if (isHit) findKnownWaterSquares(square) else null

        require(linearMatrix[shotSquareIdx] != SquareType.Hit && linearMatrix[shotSquareIdx] != SquareType.Shot) {
            "Square $square already shot."
        }

        val squares = when (searchResult) {
            is ClearSurroundingWaterSquares ->
                searchResult.shipSquares
                    .flatMap { it.getSurrounding().filterInBounds(this) }
                    .distinct()
                    .filter { get(it) == SquareType.Water }
            is ClearDiagonals -> square.getDiagonals()
            else -> emptyList()
        }
        val knownWaterSquaresIdx = squares.map { getIndexFrom(it) }.toSet()

        val newBoardList = linearMatrix.mapIndexed { idx, squareType ->
            if (idx != shotSquareIdx && idx !in knownWaterSquaresIdx) return@mapIndexed squareType

            if (isHit && idx !in knownWaterSquaresIdx)
                SquareType.Hit
            else
                SquareType.Shot
        }
        return Board(newBoardList)
    }


    /**
     * Check around the given squares to prevent adjacent ships
     * @param shipSquares the ship parts
     * @throws IllegalArgumentException if there are adjacent ships
     */
    private fun checkForAdjacentShips(shipSquares: List<Square>) {
        val seen = mutableListOf<Square>()
        val unchecked = LinkedList(shipSquares)
        seen.addAll(shipSquares)

        while (unchecked.isNotEmpty()) {
            val square = unchecked.removeFirst()
            val neighbours = square.getSurrounding().filterInBounds(this)
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
     * Gets all the squares of a Ship
     * @param initialSquare of the ship
     * @return List<Square> containing all the ship parts
     */
    private fun getShipParts(initialSquare: Square): List<Square> {
        val seen = mutableSetOf<Square>(initialSquare)
        val frontier = LinkedList<Square>()
        frontier.add(initialSquare)

        while (frontier.isNotEmpty()) {
            val square = frontier.removeFirst()
            val neighbours = square.getAxisNeighbours().filterInBounds(this)
             neighbours.filter { sqr ->
                val squareType = linearMatrix[getIndexFrom(sqr)]
                (squareType == SquareType.ShipPart || squareType == SquareType.Hit) && sqr !in seen
            }.forEach { sqr ->
                seen.add(sqr)
                frontier.add(sqr)
            }
        }
        return seen.toList()
    }


    /**
     * Checks if the index from the given square is valid
     * @param square square to check
     * @return [Int] the index of the given square
     * @throws IllegalArgumentException if the square is not in the bounds of the board
     */
    private fun requireValidIndex(square: Square): Int {
        if(!isValid(square)) {
            throw IllegalArgumentException("Square is out of bounds of the board.")
        }
        val index = getIndexFrom(square)
        require(index in linearMatrix.indices) { "The index from the specified tile is not in the bounds of the board" }
        return index
    }

    /**
     * Returns true if the square trying to be shot has a [SquareType.ShipPart] in it
     * @param square square to check
     * @return [Boolean]
     */
    private fun isHit(square: Square) = get(square) == SquareType.ShipPart

    /**
     * Checks if the ship specified by [shipSquares] can be placed.
     * Does this by checking if the board has a [SquareType.ShipPart]
     * in any of the [shipSquares] or adjacent squares
     *
     * If so throws an [IllegalArgumentException]
     *
     * @param shipSquares
     */
    @Throws(IllegalArgumentException::class)
    private fun checkCanPlaceShip(shipSquares: List<Square>) {

        val invalidSquare = shipSquares.find {
            get(it) == SquareType.ShipPart
        }

        require(invalidSquare == null){ "There is already a ship in the square $invalidSquare" }
        checkForAdjacentShips(shipSquares)
    }

    /**
     * Places a ship on the board
     * @param shipInfo the information required to represent a ship in the board
     * @returns [Board] the new board with the ship placed
     * @throws IllegalArgumentException if the square is not in the bounds of the board
     */
    fun placeShip(shipInfo: ShipInfo): Board {
        val initialSquare = shipInfo.initialSquare
        requireValidIndex(initialSquare)

        val endSquare = if(shipInfo.orientation == Orientation.Horizontal){
            Square(initialSquare.row, (initialSquare.column.ordinal + shipInfo.size - 1).column)
        } else {
            Square((initialSquare.row.ordinal + shipInfo.size - 1).row, initialSquare.column)
        }
        requireValidIndex(endSquare)

        val shipSquares = initialSquare.getBetween(finalSquare = endSquare)

        checkCanPlaceShip(shipSquares)

        val shipSquaresIndexs = shipSquares.map { square -> getIndexFrom(square) }

        return Board(
            linearMatrix.mapIndexed { idx, squareType ->
                if (shipSquaresIndexs.contains(idx)) SquareType.ShipPart
                else squareType
            }
        )
    }


}


/**
 * Place a fleet on the Board
 * @param shipInfoList fleet info
 * @return [Board] board with the fleet on it
 */
fun Board.placeShips(shipInfoList : List<ShipInfo>) : Board =
        shipInfoList.fold(this){ acc, shipInfo ->
            acc.placeShip(shipInfo)
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
 * Returns true if the board is in an end game state
 * @return [Boolean]
 */
fun Board.isInEndGameState() = linearMatrix.none { it == Board.SquareType.ShipPart }

/**
 * Returns a board filled with water
 * @param boardSide 
 * @return Board
 */
fun Board.Companion.empty(boardSide: Int) = Board(
    List(boardSide * boardSide) { Board.SquareType.Water }
)

/**
 * String representation of the board that can have the ships hidden
 * @param hiddenShips
 * @return [String] representation of the board
 */
fun Board.toLayout(hiddenShips : Boolean = false): String = linearMatrix.joinToString("") {
    if (hiddenShips && it == Board.SquareType.ShipPart)
        Board.SquareType.Water.toString()
    else
        it.toString()
}


/**
 * @return [String] representation of the Board
 */
fun Board.toLayout(): String = toLayout(false)




