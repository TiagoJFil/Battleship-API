package pt.isel.daw.battleship.services

import pt.isel.daw.battleship.data.*
import pt.isel.daw.battleship.data.model.*


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
    val gameservice = GameService(FakeGameRepo(), FakeBoardRepo())

    var game = Game(gameID, Game.State.PLAYING, turnIdx = 0, boards=List(2){Board.fromLayout(boardLayout)})

    gameservice.gameRepo.updateGame(game.Id,game)

  //  game = game.makeShot(Square(0.row, 0.column))

    gameservice.makeShots(listOf(Square(1.row, 2.column)), 1, gameID)

    gameservice.gameRepo.getGame(gameID)?.boards?.forEach { println(it.pretty() + "\n") }

}


class GameService(
        val gameRepo: GameRepository,
        val boardRepo : BoardRepository,
) {

    //Allow a user to define a set of shots on each round.
    fun makeShots(tiles: List<Square>, userId: Id, gameId: Id){
        //list because it depends on the number of shots of the game
        val game = gameRepo.getGame(gameId) ?: throw Exception("Game not found")
        val uid = game.turnPlayer.id
        if(uid != userId) throw Exception("Not your turn")

        val newGame = game.makeShot(tiles)

     //   boardRepo.updateBoard(gameId, newBoard)
        gameRepo.updateGame(gameId,newGame)
    }


    //Allow a user to define the layout of their fleet in the grid.
    fun setBoardLayout(shipList: List<ShipInfo>, userId: Id, gameId : Id){
        val game = gameRepo.getGame(gameId) ?: throw Exception("Game not found")
        val uid = game.turnPlayer.id
        if(uid != userId) throw Exception("Not your turn")


        val newGame = game.placeShips(shipList)
        gameRepo.updateGame(gameId,newGame)
    }


    fun queueGame(user: Id){

        //repo.getUser(user)
        //verify user id

        //ver se tem algum game running com esse user
        //queue user
    }

    fun cancelQueue(user: Id){

        //verify user id
        //ver se o user ta queued
        //remove user from queue
    }

    //Inform the user about the overall state of a game, namely: game phase (layout definition phase, shooting phase, completed phase).
    fun getGameState(game : Id){

        //verify game id
        //return game state
    }





}
