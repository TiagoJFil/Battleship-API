package pt.isel.daw.battleship.domain

/**
 * Represents a Square
 */
data class Square(val row: Row, val column: Column){
    constructor(rowOrdinal: Int, columnOrdinal: Int) : this(Row(rowOrdinal), Column(columnOrdinal))
}

data class Row(val ordinal: Int) {
    operator fun minus(other: Row): Int = ordinal - other.ordinal
}

data class Column(val ordinal: Int) {
    operator fun minus(other: Column): Int = ordinal - other.ordinal
}

val Int.row get() = Row(this)
val Int.column get() = Column(this)

/**
 * Gets the neighbours of a square on the y-axis and x-axis
 *
 * @receiver the square to get the neighbours from
 * @return [List] containing the vertical and horizontal neighbours of the square
 */
fun Square.getAxisNeighbours(): List<Square> {
    val top = Square((row.ordinal - 1), column.ordinal)
    val bottom = Square((row.ordinal + 1), column.ordinal)
    val left = Square(row.ordinal, (column.ordinal - 1))
    val right = Square(row.ordinal, (column.ordinal + 1))

    return listOf(
        top, bottom, left, right
    )
}

/**
 * Gets the neighbours of a square
 * @return List<Square> diagonal, vertical and horizontal neighbours of the ship
 */
fun Square.getSurrounding() : List<Square> = this.getAxisNeighbours() + this.getDiagonals()

/**
 * Gets the diagonal neighbours of a square
 * @return List<Square> containing the diagonal neighbours of the ship
 */
fun Square.getDiagonals(): List<Square> {
    val topLeft = Square((row.ordinal - 1), (column.ordinal - 1))
    val topRight = Square((row.ordinal - 1), (column.ordinal + 1))
    val bottomLeft = Square((row.ordinal + 1), (column.ordinal - 1))
    val bottomRight = Square((row.ordinal + 1), (column.ordinal + 1))

    return listOf(
        topLeft, topRight, bottomLeft, bottomRight
    )
}

/**
 * Gets the squares between the initial and final square included
 * @receiver the initial square
 * @param finalSquare the final square
 *
 * @return the squares between the initial and final square included
 */
fun Square.getBetween(finalSquare: Square): List<Square> {
    val squareList = mutableListOf<Square>()
    val squaresVector = Vector(this, finalSquare)
    val initialSquare = this

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


