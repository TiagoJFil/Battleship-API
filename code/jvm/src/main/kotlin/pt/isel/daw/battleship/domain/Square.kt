package pt.isel.daw.battleship.domain

import java.lang.Math.abs

/**
 * Represents a Square
 */
data class Square(val row: Row, val column: Column){
    constructor(row: Int, column: Int) : this(Row(row), Column(column))
}


data class Row(val ordinal: Int) {
    operator fun minus(other: Row): Int = ordinal - other.ordinal
}

data class Column(val ordinal: Int) {
    operator fun minus(other: Column): Int = ordinal - other.ordinal
}

val Int.row get() = Row(this)
val Int.column get() = Column(this)

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
            val verticalSize = kotlin.math.abs(initialSquare.row - finalSquare.row)
            val horizontalSize = kotlin.math.abs(initialSquare.column - finalSquare.column)

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

