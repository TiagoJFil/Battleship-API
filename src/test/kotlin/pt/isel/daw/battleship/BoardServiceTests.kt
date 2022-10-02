package pt.isel.daw.battleship

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.platform.commons.logging.LoggerFactory
import pt.isel.daw.battleship.data.Square
import pt.isel.daw.battleship.data.column
import pt.isel.daw.battleship.data.model.Board
import pt.isel.daw.battleship.data.model.prettyPrint
import pt.isel.daw.battleship.data.row

class BoardServiceTests {

    companion object{
        const val FOUR_BY_FOUR_EMPTY_LAYOUT = "####" +
                                       "####" +
                                       "####" +
                                       "####"

        private val logger = LoggerFactory.getLogger(this::class.java)

    }

    @Test
    fun `Trying to create a board with an empty layout`(){
        assertThrows<IllegalArgumentException> {
            Board.fromLayout("")
        }
    }

    @Test
    fun `Creating a board with an invalid tile representation gives IllegalArgumentException`(){

        val layout = "##" +
                     "'#"

        assertThrows<IllegalArgumentException> {
            Board.fromLayout(layout)
        }

    }

    @Test
    fun `Creating a board that does not represent a square gives IllegalArgumentException`(){

        val layout = "####" +
                     "####"

        assertThrows<IllegalArgumentException> {
            Board.fromLayout(layout)
        }

    }

    @Test
    fun `Creating a board from a valid layout`(){

        val layout = FOUR_BY_FOUR_EMPTY_LAYOUT

        val board = Board.fromLayout(layout)

        val expected = List(layout.length){ Board.SquareType.Water }


        assert(board.matrix == expected)

    }


    @Test
    fun `Shooting on water gives back a board with a hit`() {

        val layout = FOUR_BY_FOUR_EMPTY_LAYOUT

        val board = Board.fromLayout(layout)
        val targetSquare = Square(0.row, 0.column)


        val newBoard = board.shotTo(targetSquare)

        val expectedLayout = Board.SquareType.Shot.representation + "###" +
                             "####" +
                             "####" +
                             "####"

        val expectedBoard = Board.fromLayout(expectedLayout)

        assert(newBoard == expectedBoard)

    }

    @Test
    fun `Multiple shots on water always return shots`(){

        val targetSquares = listOf(
            Square(0.row, 0.column),
            Square(1.row, 0.column),
            Square(2.row, 0.column),
            Square(3.row, 0.column)
        )

        val shotRepre = Board.SquareType.Shot.representation
        val layout = FOUR_BY_FOUR_EMPTY_LAYOUT

        val expectedLayout = shotRepre + "###" +
                             shotRepre + "###" +
                             shotRepre + "###" +
                             shotRepre + "###"

        val expectedBoard = Board.fromLayout(expectedLayout)

        val finalBoard = targetSquares.fold(Board.fromLayout(layout)){ boardAccumulator, square ->
            boardAccumulator.shotTo(square)
        }

        logger.info { "\n" + finalBoard.prettyPrint() }

        assert(finalBoard == expectedBoard)

    }


}