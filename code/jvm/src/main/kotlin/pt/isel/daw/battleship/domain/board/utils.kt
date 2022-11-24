package pt.isel.daw.battleship.domain.board

import pt.isel.daw.battleship.domain.Square

/**
 * Returns a list of the squares from the original list that are in bounds of the board
 */
fun List<Square>.filterInBounds(board: Board): List<Square> = filter {
        board.isValid(it) && board.getIndexFrom(it) in board.linearMatrix.indices
    }


/**
 * Checks if the square is valid in the board's context
 *
 * @param square square to check
 * @return [Boolean] true if the square is valid, false otherwise
 */
fun Board.isValid(square: Square): Boolean{
    val validRange = 0 until side
    return square.row.ordinal in validRange && square.column.ordinal in validRange
}

/**
 * Gets the index from a given square in the Board
 * @param square square to check
 * @return [Int] the index of the given square
 */
fun Board.getIndexFrom(square: Square): Int = square.row.ordinal * side + square.column.ordinal

/**
 * Returns a square from an index of the board in linear representation.
 *
 * @param index the index of the square in the board
 * @return the square
 */
fun Board.indexToSquare(index: Int): Square {
    val rowOrdinal = index / side
    return Square(
        rowOrdinal, index - rowOrdinal * side
    )
}
