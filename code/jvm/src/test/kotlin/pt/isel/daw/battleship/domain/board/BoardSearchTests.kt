package pt.isel.daw.battleship.board

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.domain.Column
import pt.isel.daw.battleship.domain.Row
import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.domain.board.Board
import pt.isel.daw.battleship.domain.board.ClearDiagonals
import pt.isel.daw.battleship.domain.board.ClearSurroundingWaterSquares
import pt.isel.daw.battleship.domain.board.findKnownWaterSquares

class BoardSearchTests {
    @Test
    fun `Get known water squares`() {
        val layout = "######" +
                "###B##" +
                "###B##" +
                "###X##" +
                "###B##" +
                "######"
        val board = Board.fromLayout(layout)
        val square = Square(Row(2), Column(3))

        val result = board.findKnownWaterSquares(square)

        assert(result is ClearDiagonals)
    }

    @Test
    fun `Get known water squares with only one ship part to hit`() {
        val layout = "######" +
                "###X##" +
                "###X##" +
                "###X##" +
                "###B##" +
                "######"
        val board = Board.fromLayout(layout)
        val square = Square(Row(4), Column(3))

        val result = board.findKnownWaterSquares(square)

        val expected = ClearSurroundingWaterSquares(
            listOf(
                square,
                square.copy(Row(3)),
                square.copy((Row(2))),
                square.copy(Row(1))
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `Get known water squares with shots in the water`() {
        val layout = "##O#O#" +
                "##OXO#" +
                "##OXO#" +
                "##OXO#" +
                "##OBO#" +
                "######"
        val board = Board.fromLayout(layout)
        val square = Square(Row(4), Column(3))

        val result = board.findKnownWaterSquares(square)

        val expected = ClearSurroundingWaterSquares(
            listOf(
                square,
                square.copy(Row(3)),
                square.copy((Row(2))),
                square.copy(Row(1))
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `Get known water squares of a ship with size 1`() {
        val layout = "######" +
                "######" +
                "######" +
                "######" +
                "###B##" +
                "######"

        val board = Board.fromLayout(layout)
        val square = Square(Row(4), Column(3))

        val result = board.findKnownWaterSquares(square)

        val expected = ClearSurroundingWaterSquares(
            listOf(
                square,
            )
        )
        assertEquals(expected, result)
    }

    @Test
    fun `Get known water squares with a ship near the corner that has already been hit`() {
        val layout = "######" +
                "B#####" +
                "X#####" +
                "B#####" +
                "######" +
                "######"
        val board = Board.fromLayout(layout)
        val square = Square(Row(1), Column(0))

        val result = board.findKnownWaterSquares(square)

        val expected = ClearDiagonals

        assertEquals(expected, result)
    }
}