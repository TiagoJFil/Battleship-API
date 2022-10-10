package pt.isel.daw.battleship.board

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.platform.commons.logging.LoggerFactory
import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.domain.column
import pt.isel.daw.battleship.domain.model.Board
import pt.isel.daw.battleship.domain.model.makeShots
import pt.isel.daw.battleship.domain.model.pretty
import pt.isel.daw.battleship.domain.row

class BoardShotTests {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }
    @Test
    fun `Shooting on water gives back a board with a hit`() {

        val layout = BoardTests.FOUR_BY_FOUR_EMPTY_LAYOUT

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
    fun `Multiple shots on water always return shots`() {

        val targetSquares = listOf(
            Square(0.row, 0.column),
            Square(1.row, 0.column),
            Square(2.row, 0.column),
            Square(3.row, 0.column)
        )

        val shotRepre = Board.SquareType.Shot.representation
        val layout = BoardTests.FOUR_BY_FOUR_EMPTY_LAYOUT

        val expectedLayout = shotRepre + "###" +
                shotRepre + "###" +
                shotRepre + "###" +
                shotRepre + "###"

        val expectedBoard = Board.fromLayout(expectedLayout)

        val finalBoard = targetSquares.fold(Board.fromLayout(layout)) { boardAccumulator, square ->
            boardAccumulator.shotTo(square)
        }

        logger.info { "\n" + expectedBoard.pretty() }
        logger.info { "\n" + finalBoard.pretty() }

        assert(finalBoard == expectedBoard)

    }

    @Test
    fun `Shot on a ship part gives back a board with a hit and shots around in the diagonals`(){
        val layout = "##B#" +
                "##B#" +
                "##B#" +
                "####"

        val board = Board.fromLayout(layout)

        val targetSquare = Square(1.row, 2.column)

        val newBoard = board.shotTo(targetSquare)

        val expectedLayout =
                "#OBO" +
                "##X#" +
                "#OBO" +
                "####"

        val expectedBoard = Board.fromLayout(expectedLayout)

        Assertions.assertEquals(expectedBoard.matrix, newBoard.matrix)
    }

    @Test
    fun `Shot on a ship part gives back a board with a hit and shots around the ship`(){
        val layout =
                "#OXO" +
                "#OXO" +
                "#OBO" +
                "####"


        val board = Board.fromLayout(layout)
        val targetSquare = Square(2.row, 2.column)


        val newBoard = board.shotTo(targetSquare)

        val expectedLayout =
                "#OXO" +
                "#OXO" +
                "#OXO" +
                "#OOO"

        val expectedBoard = Board.fromLayout(expectedLayout)

        Assertions.assertEquals(expectedBoard.matrix, newBoard.matrix)
    }

    @Test
    fun `Shot on a ship with only 1 part gives back a board with a hit and shots around the ship`(){
        val layout =
                "####" +
                "##B#" +
                "####" +
                "####"



        val board = Board.fromLayout(layout)
        val targetSquare = Square(1.row, 2.column)


        val newBoard = board.shotTo(targetSquare)
        newBoard.pretty().let{ println(it)}
        val expectedLayout =
                "#OOO" +
                "#OXO" +
                "#OOO" +
                "####"

        val expectedBoard = Board.fromLayout(expectedLayout)

        Assertions.assertEquals(expectedBoard.matrix, newBoard.matrix)
    }

    @Test
    fun `Multiple shots on the board`(){
        val layout =
                "##B##" +
                "##B##" +
                "##B##" +
                "#####" +
                "#####"

        val board = Board.fromLayout(layout)
        val newBoard = board.makeShots(listOf(
                Square(0.row,2.column),
                Square(1.row,2.column),
                Square(2.row,0.column),
        ))

        val expectedLayout =
                "#OXO#" +
                "#OXO#" +
                "OOBO#" +
                "#####" +
                "#####"

        val expectedBoard = Board.fromLayout(expectedLayout)

        Assertions.assertEquals(expectedBoard.matrix, newBoard.matrix)
    }

    @Test
    fun `Cant shoot twice on the same square`(){
        val layout =
                "###" +
                "#O#" +
                "###"

        val board = Board.fromLayout(layout)


        Assertions.assertThrows(IllegalArgumentException::class.java){
            val newBoard = board.shotTo(Square(1.row,1.column))

        }
    }

    @Test
    fun `Cant shoot on a square that is out of bounds`(){
        val layout =
                "###" +
                "#O#" +
                "###"

        val board = Board.fromLayout(layout)


        Assertions.assertThrows(IllegalArgumentException::class.java){
            val newBoard = board.shotTo(Square(5.row,1.column))

        }
    }

    @Test
    fun `Cant shoot twice on the same square that had a boat`(){
        val layout =
                        "###" +
                        "#X#" +
                        "###"

        val board = Board.fromLayout(layout)


        Assertions.assertThrows(IllegalArgumentException::class.java){
            val newBoard = board.shotTo(Square(1.row,1.column))

        }
    }
}