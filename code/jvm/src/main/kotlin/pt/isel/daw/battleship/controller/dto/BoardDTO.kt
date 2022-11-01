package pt.isel.daw.battleship.controller.dto

import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.utils.UserID

data class BoardDTO(
    val userID: UserID,
    val shipParts: List<Square>,
    val shots: List<Square>,
    val boardSide: Int,
)
