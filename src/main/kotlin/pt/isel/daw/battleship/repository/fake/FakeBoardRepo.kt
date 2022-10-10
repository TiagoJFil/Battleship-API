package pt.isel.daw.battleship.repository.fake

import pt.isel.daw.battleship.data.model.Id
import pt.isel.daw.battleship.data.model.Board
import pt.isel.daw.battleship.repository.BoardRepository

class FakeBoardRepo : BoardRepository {

    private val table = mutableMapOf<Id, Board>()

    override fun getBoard(boardId: Int): Board {
        return table[boardId] ?: throw Exception("Board not found")
    }

    override fun updateBoard(boardId: Int, board: Board) {
        table[boardId] = board
    }

}
