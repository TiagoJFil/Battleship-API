package pt.isel.daw.battleship.domain.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pt.isel.daw.battleship.domain.*


class GameTests {


    private val testGameRules = GameRules(
        1,
        4,
        0,
        0,
        GameRules.ShipRules(
            "test", mapOf(2 to 1)
        )
    )

    private val testBoardLayout = "####" +
                                  "##B#" +
                                  "##B#" +
                                  "####"

    private val emptyBoard = "####" +
                             "####" +
                             "####" +
                             "####"


    @Test
    fun `Make play with number of shots different from the rules`(){

        val boards = listOf(0, 1).associateWith { Board.fromLayout(testBoardLayout) }

        val game = Game(0, Game.State.PLAYING, rules = testGameRules, boards, 0)

        assertThrows<IllegalArgumentException> {
            game.makePlay(listOf(Square(0, 0), Square(1, 0)))
        }

    }

    @Test
    fun `Creating a game with a board with a board's side length different from the rules`(){

        val boards = listOf(0, 1).associateWith { Board.fromLayout("#####" +
                                                                 "##B##" +
                                                                 "##B##" +
                                                                 "#####" +
                                                                 "#####") }

        assertThrows<IllegalArgumentException> {
            Game(0, Game.State.PLAYING, rules = testGameRules, boards, 0)
        }

    }


    @Test
    fun `Make a play and check the boards are correct`(){

        val startingBoard = Board.fromLayout("####" +
                "##B#" +
                "##B#" +
                "####"
        )

        val boards = listOf(0, 1).associateWith { startingBoard }

        val expectedBoard = Board.fromLayout("O###" +
                "##B#" +
                "##B#" +
                "####"
           )

        val expectedBoards = mapOf(
                0 to startingBoard,
                1 to expectedBoard
        )

        val expectedGame = Game(0, Game.State.PLAYING, rules=testGameRules, expectedBoards, 1)

        val actualGame = Game(0, Game.State.PLAYING, rules = testGameRules, boards, 0)
                .makePlay(listOf(Square(0,0)))

        assertEquals(expectedGame, actualGame)
    }

    @Test
    fun `Placing ships on invalid squares throws an exception`(){
        val testBoard = Board.fromLayout(testBoardLayout)
        val game = Game(0, Game.State.PLACING_SHIPS,rules= testGameRules, boards = mapOf(1 to testBoard, 2 to testBoard), turnID= 2)
        val shipInfo = ShipInfo(Square(10,20), 5, Orientation.Horizontal)
        assertThrows<IllegalArgumentException> {
            game.placeShips(listOf(shipInfo), 1)
        }
    }

    @Test
    fun `placing ships on valid squares works correctly`(){
        val testBoard = Board.fromLayout(emptyBoard)
        val game = Game(0, Game.State.PLACING_SHIPS,rules= testGameRules, boards = mapOf(1 to testBoard, 2 to testBoard), turnID= 2)
        val shipInfo = ShipInfo(Square(1,1), 2, Orientation.Vertical)
        val newGame = game.placeShips(listOf(shipInfo), 2)
        val expectedBoard = Board.fromLayout(
                        "####" +
                        "#B##" +
                        "#B##" +
                        "####"
        )
        val newBoards = mapOf(
            1 to testBoard,
            2 to expectedBoard
        )
        val expectedGame = Game(0, Game.State.PLACING_SHIPS, rules=testGameRules, boards= newBoards, turnID=2)
        assertEquals(expectedGame, newGame)
    }

    @Test
    fun `Starting a game with a board that does not respect the rules fails`(){
        val invalidLayout = "###B" +
                           "#B##" +
                           "#B##" +
                           "###B"

        val invalidBoard = Board.fromLayout(invalidLayout)
        val validBoard = Board.fromLayout(testBoardLayout)

        assertThrows<java.lang.IllegalStateException> {
            Game(0, Game.State.PLAYING, rules= testGameRules, boards = mapOf(1 to validBoard, 2 to invalidBoard), turnID= 2)
        }
    }

    @Test
    fun `Making the last play on a game ends it`(){
        val layout = "O#O#" +
                     "#X##" +
                     "OBO#" +
                     "####"

        val expectedLayout = "OOO#" +
                             "OXO#" +
                             "OXO#" +
                             "OOO#"

        val boards = listOf(1, 2).associateWith { Board.fromLayout(layout) }
        val expectedBoards = mapOf(
            1 to Board.fromLayout(expectedLayout),
            2 to Board.fromLayout(layout)
        )
        val newGame = Game(0, Game.State.PLAYING, rules=testGameRules, boards= boards, turnID= 2)
                        .makePlay(listOf(Square(2,1)))

        val expectedGame = Game(0, Game.State.FINISHED, rules=testGameRules, boards= expectedBoards, turnID= 1)

        assertEquals(expectedGame, newGame)

    }

}