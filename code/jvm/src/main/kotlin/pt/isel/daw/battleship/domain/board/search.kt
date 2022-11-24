package pt.isel.daw.battleship.domain.board

import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.domain.getAxisNeighbours
import java.util.LinkedList


sealed class BoardSearchResult
object ClearDiagonals : BoardSearchResult()
data class ClearSurroundingWaterSquares(val shipSquares: List<Square>) : BoardSearchResult()

/**
 * Searches for the known water squares after a hit and returns a [BoardSearchResult] that shows its format:
 * - All around
 * - Diagonal
 * @param initialSquare of the ship
 * @return [BoardSearchResult]
 */
fun Board.findKnownWaterSquares(initialSquare: Square): BoardSearchResult {
    val seen = mutableSetOf<Square>()
    val frontier = LinkedList<Square>()

    frontier.add(initialSquare)

    while (frontier.isNotEmpty()) {
        val square = frontier.removeFirst()
        val neighbours = square
            .getAxisNeighbours()
            .filterInBounds(board=this)

        if (neighbours.any { this[it] == Board.SquareType.ShipPart && it !in seen }) return ClearDiagonals

        val hits = neighbours.filter { this[it] == Board.SquareType.Hit && it !in seen }
        seen.add(square)
        frontier.addAll(hits)
    }

    return ClearSurroundingWaterSquares(seen.toList())
}


