package pt.isel.daw.battleship.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.daw.battleship.model.Board
import pt.isel.daw.battleship.repository.BoardRepository

class JdbiBoardsRepository(
    private val handle: Handle
) : BoardRepository {
    override fun getBoard(boardId: Int): Board {
        TODO("Not yet implemented")
    }

    override fun updateBoard(boardId: Int, board: Board) {
        TODO("Not yet implemented")
    }
}