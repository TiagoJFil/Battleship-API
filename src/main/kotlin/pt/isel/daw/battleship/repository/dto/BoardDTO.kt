package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.domain.Board
import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.utils.UserID

/**
 * A Board representation containing all the relevant information to the client
 */
data class BoardDTO(
    val userID: UserID,
    val shipParts: List<Square>,
    val shots: List<Square>,
    val hits: List<Square>,
    val boardSide: Int,
)

/**
 * Converts a [Board] to a [BoardDTO]
 */
fun Board.toDTO(userID: UserID): BoardDTO {

    data class SquareInfo(val square: Square, val type: Board.SquareType)

    val squaresToSquareType = matrix.mapIndexed { idx, squareType ->
        SquareInfo(indexToSquare(idx), squareType)
    }

    val getAllSquaresByType: (Board.SquareType) -> List<Square> = { st: Board.SquareType ->
        squaresToSquareType.filter { it.type == st }.map { it.square }
    }

    return BoardDTO(
        userID = userID,
        shipParts = getAllSquaresByType(Board.SquareType.ShipPart),
        shots = getAllSquaresByType(Board.SquareType.Shot),
        hits = getAllSquaresByType(Board.SquareType.Hit),
        boardSide = side
    )
}