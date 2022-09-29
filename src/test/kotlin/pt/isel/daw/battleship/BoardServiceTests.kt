package pt.isel.daw.battleship

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.data.Board

class BoardServiceTests {

    @Test
    fun `Trying to create a board with an empty layout`(){
        assertThrows<IllegalArgumentException> {
            Board.fromLayout("")
        }
    }

    @Test
    fun `Creating a board with an invalid tile representation gives IllegalArgumentException`(){

        val layout = "##" +
                     "N#"

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

        val layout = "####" +
                     "####" +
                     "####" +
                     "####"

        val board = Board.fromLayout(layout)

        val expected = List(layout.length){ Board.Tile.Water }


        assert(board.board == expected)

    }

}