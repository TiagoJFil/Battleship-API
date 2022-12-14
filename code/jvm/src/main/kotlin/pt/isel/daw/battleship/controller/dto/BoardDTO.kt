package pt.isel.daw.battleship.controller.dto

import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.domain.board.Board
import pt.isel.daw.battleship.domain.board.indexToSquare
import pt.isel.daw.battleship.services.GameService
import pt.isel.daw.battleship.utils.UserID

/**
 * A Board representation containing all the relevant information to the client
 */
data class BoardDTO(
    val userID: UserID,
    val shipParts: List<Square>,
    val shots: List<Square>,
    val hits: List<Square>,
    val side: Int,
)

/**
 * Converts a [Board] to a controller [BoardDTO]
 */
fun Board.toDTO(userID: UserID, fleet: GameService.Fleet): BoardDTO {

    data class SquareInfo(val square: Square, val type: Board.SquareType)

    val squaresToSquareType = linearMatrix.mapIndexed { idx, squareType ->
        SquareInfo(indexToSquare(idx), squareType)
    }

    val getAllSquaresByType: (Board.SquareType) -> List<Square> = { squareType: Board.SquareType ->
        squaresToSquareType.filter { it.type == squareType }.map { it.square }
    }

    return BoardDTO(
        userID = userID,
        shipParts = if(fleet == GameService.Fleet.MY) getAllSquaresByType(Board.SquareType.ShipPart) else emptyList(),
        shots = getAllSquaresByType(Board.SquareType.Shot),
        hits = getAllSquaresByType(Board.SquareType.Hit),
        side = side
    )
}
