package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.domain.model.Board

class FakeBoardRepo : BoardRepository {

    private val table = mutableMapOf<Id, Board>()

    override fun getBoard(boardId: Int): Board {
        return table[boardId] ?: throw Exception("Board not found")
    }

    override fun updateBoard(boardId: Int, board: Board) {
        table[boardId] = board
    }

}
