package pt.isel.daw.battleship.domain

import kotlin.math.abs

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