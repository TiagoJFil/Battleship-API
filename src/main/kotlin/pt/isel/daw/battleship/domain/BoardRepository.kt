package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.domain.model.Board


interface BoardRepository {
    fun getBoard(boardId : Int) : Board

    fun updateBoard(boardId : Int, board : Board)
}