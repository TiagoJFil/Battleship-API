package pt.isel.daw.battleship.services

import pt.isel.daw.battleship.data.Square
import pt.isel.daw.battleship.data.column
import pt.isel.daw.battleship.data.model.*
import pt.isel.daw.battleship.data.row


fun main() {

    val gameID = 0
    val boardLayout =   "##########" +
                        "##B###BB##" +
                        "##B#######" +
                        "##B#######" +
                        "##########" +
                        "##BBBBB###" +
                        "#######B##" +
                        "##BB###B##" +
                        "#######B##" +
                        "#######B##"

    var game = Game(gameID, Game.State.PLAYING, turnIdx = 0, boards=List(2){Board.fromLayout(boardLayout)})


    game = game.makeShot(Square(0.row, 0.column))





}