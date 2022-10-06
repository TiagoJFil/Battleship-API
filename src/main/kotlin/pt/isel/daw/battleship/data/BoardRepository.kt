package pt.isel.daw.battleship.data

import pt.isel.daw.battleship.data.model.Board


interface BoardRepository {
    fun getBoard(boardId : Int) : Board

    fun updateBoard(boardId : Int, board : Board)
}