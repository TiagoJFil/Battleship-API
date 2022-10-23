package pt.isel.daw.battleship.repository.dto

import pt.isel.daw.battleship.model.Board
import pt.isel.daw.battleship.model.Square
import pt.isel.daw.battleship.utils.UserID

data class BoardDTO(
    val userID: UserID,
    val shipParts: List<Square>,
    val shots: List<Square>,
    val boardSide: Int,
)

fun Board.toDTO(userID: UserID): BoardDTO {

    val squares = matrix.mapIndexed{ idx, squareType->
        indexToSquare(idx) to squareType
    }

    val shipParts = squares.filter { it.second == Board.SquareType.ShipPart }.map { it.first }
    val shots = squares.filter { it.second == Board.SquareType.Shot }.map { it.first }

    return BoardDTO(
        userID = userID,
        shipParts = shipParts,
        shots = shots,
        boardSide = side
    )
}