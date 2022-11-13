package pt.isel.daw.battleship.domain.board

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.platform.commons.logging.LoggerFactory
import pt.isel.daw.battleship.domain.Column
import pt.isel.daw.battleship.domain.Row
import pt.isel.daw.battleship.domain.Square
import pt.isel.daw.battleship.domain.column
import pt.isel.daw.battleship.domain.row
import pt.isel.daw.battleship.domain.*

class BoardTests {

    companion object {
        const val FOUR_BY_FOUR_EMPTY_LAYOUT =
                "####" +
                "####" +
                "####" +
                "####"

        private val logger = LoggerFactory.getLogger(BoardTests::class.java)
    }

    @Test
    fun `Trying to create a board with an empty layout`() {
        assertThrows<IllegalArgumentException> {
            Board.fromLayout("")
        }
    }

    @Test
    fun `Creating a board with an invalid tile representation gives IllegalArgumentException`() {

        val layout =
                "##" +
                "'#"

        assertThrows<IllegalArgumentException> {
            Board.fromLayout(layout)
        }

    }

    @Test
    fun `Creating a board that does not represent a square gives IllegalArgumentException`() {

        val layout =
                "####" +
                "####"

        assertThrows<IllegalArgumentException> {
            Board.fromLayout(layout)
        }

    }

    @Test
    fun `Creating a board from a valid layout`() {

        val layout = FOUR_BY_FOUR_EMPTY_LAYOUT

        val board = Board.fromLayout(layout)

        val expected = List(layout.length) { Board.SquareType.Water }


        assert(board.matrix == expected)

    }

    @Test
    fun `Get the tile type from a given square from its index`() {
        val square1 = Square(Row(0), Column(4))
        val square2 = Square(Row(5), Column(0))
        val square3 = Square(Row(3), Column(4))

        val layout =
                "####B#" +
                "######" +
                "######" +
                "######" +
                "######" +
                "X#####"

        val board = Board.fromLayout(layout)

        val tile1 = board.get(square1)
        val tile2 = board.get(square2)
        val tile3 = board.get(square3)

        assertEquals(Board.SquareType.ShipPart, tile1)
        assertEquals(Board.SquareType.Hit, tile2)
        assertEquals(Board.SquareType.Water, tile3)
    }

    @Test
    fun `Getting the index of a square outside the board throws IllegalArgument`() {
        assertThrows<IllegalArgumentException> {
            val square = Square(Row(50), Column(20))

            val layout =
                    "####" +
                    "####"

            val board = Board.fromLayout(layout)

            board.get(square)
        }
    }

    @Test
    fun `Board to String representation`() {
        val layout =
                "#BX#" +
                "O###" +
                "#BBX" +
                "#O#O"

        val board = Board.fromLayout(layout)

        assertEquals("#BX#O####BBX#O#O", board.toLayout())
    }

    @Test
    fun `Shot to a tile`() {
        val layout =
                "####" +
                "####" +
                "####" +
                "####"

        val board = Board.fromLayout(layout)
        val newBoard = board.shotTo(Square(Row(2), Column(3)))

        val newLayout =
                "####" +
                "####" +
                "###O" +
                "####"

        val expectedNewBoard = Board.fromLayout(newLayout)

        assert(expectedNewBoard.matrix == newBoard.matrix)
    }

    @Test
    fun `placeShip works correctly`() {
        val layout =
                "######" +
                "######" +
                "######" +
                "######" +
                "######" +
                "######"

        val board = Board.fromLayout(layout)
        val ship = ShipInfo(Square(0.row, 0.column),3, Orientation.Horizontal)
        val finalBoard = board.placeShip(ship)

        println(finalBoard)

        val expected =
                "BBB###" +
                "######" +
                "######" +
                "######" +
                "######" +
                "######"
        assert(finalBoard.matrix == Board.fromLayout(expected).matrix)
    }


    @Test
    fun `placeShips works`(){
        val layout =
                "######" +
                "######" +
                "######" +
                "######" +
                "######" +
                "######"

        val board = Board.fromLayout(layout)

        val finalBoard = board.placeShips(
                listOf(
                        ShipInfo(Square(0.row, 0.column), 4, Orientation.Horizontal ),
                        ShipInfo(Square(2.row, 1.column), 3, Orientation.Vertical ),
                )
        )

        val expected =
                "BBBB##" +
                "######" +
                "#B####" +
                "#B####" +
                "#B####" +
                "######"

        assert(finalBoard.matrix == Board.fromLayout(expected).matrix)
    }

    @Test
    fun `cant place a ship next to another`(){
        val layout =
                "######" +
                "######" +
                "##BBB#" +
                "######" +
                "#B####" +
                "#B####"

        val board = Board.fromLayout(layout)
        assertThrows<IllegalArgumentException> {
            board.placeShip(
                ShipInfo(
                    Square(Row(0), Column(1)), 2, Orientation.Vertical
                )
            )
        }

    }

    @Test
    fun `cant place a ship outside the board`(){
        assertThrows<IllegalArgumentException> {
            val board = Board.fromLayout(FOUR_BY_FOUR_EMPTY_LAYOUT)
            board.placeShip(
                ShipInfo(
                    Square(Row(0), Column(3)), 2, Orientation.Horizontal
                )
            )
        }
    }

    @Test
    fun `cant place a ship on top of another`(){

        val layout =
                "#BBB##" +
                "######" +
                "######" +
                "######" +
                "######" +
                "######"

        val board = Board.fromLayout(layout)

        assertThrows<IllegalArgumentException> {
            board.placeShip(
                ShipInfo(
                    Square(0.row, 0.column), 3, Orientation.Horizontal
                )
            )

        }
    }

    @Test
    fun `get index from square test`(){

        val board = Board.fromLayout(FOUR_BY_FOUR_EMPTY_LAYOUT)
        val squares = listOf(
            Square(Row(0), Column(0)),
            Square(Row(0), Column(1)),
            Square(Row(0), Column(2)),
            Square(Row(0), Column(3)),
            Square(Row(1), Column(0)),
            Square(Row(1), Column(1)),
            Square(Row(1), Column(2)),
            Square(Row(1), Column(3)),
            Square(Row(2), Column(0)),
            Square(Row(2), Column(1)),
            Square(Row(2), Column(2)),
            Square(Row(2), Column(3)),
            Square(Row(3), Column(0)),
            Square(Row(3), Column(1)),
            Square(Row(3), Column(2)),
            Square(Row(3), Column(3)),
        )

        val expected = listOf(0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15)

        squares.forEachIndexed { index, square ->
            logger.info { "index: $index, square: $square" }
            assertEquals(expected[index], board.getIndexFrom(square))
        }

    }

    @Test
    fun `SquareType toString gives the same as the representation`(){
        val expected = Board.SquareType.values().associateWith { it.representation.toString() }
        val actual = Board.SquareType.values().associateWith { it.toString() }
        assertEquals(expected, actual)
    }

    @Test
    fun `getting the layout from a board with hiddenShips gives back a board with only water`(){

        val layout =
                "######" +
                "######" +
                "##BBB#" +
                "######" +
                "#B####" +
                "#B####"

        val board = Board.fromLayout(layout)

        val expected =
                "######" +
                "######" +
                "######" +
                "######" +
                "######" +
                "######"


        assertEquals(expected, board.toLayout(hiddenShips = true))

    }


}


