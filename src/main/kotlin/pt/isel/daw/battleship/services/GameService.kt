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

    var game = Game(gameID, Game.State.PLAYING, turnIdx = 0, boards=List(2){Board.fromLayout(boardLayout)})


    game = game.makeShot(Square(0.row, 0.column))





}


class GameService(
        val repository: GameRepository,
        val boardRepo : BoardRepository,
) {

    //Allow an user to define a set of shots on each round.
    fun makeShoot(tiles: List<Square>, userId: Id, gameId: Id){
        //list because it depends on the number of shots of the game
        val game = repository.getGame(gameId) ?: throw Exception("Game not found")
        val uid = game.turnPlayer.id
        if(uid != userId) throw Exception("Not your turn")

        val board = game.oppositeTurnBoard
        val newBoard = board.makeShots(tiles)

        boardRepo.updateBoard(gameId, newBoard)
    }

    //Allow an user to define the layout of their fleet in the grid.
    fun setBoardLayout(boatList: List<Pair<Square,Square>>, userId: Id, gameId : Id){
        val game = repository.getGame(gameId) ?: throw Exception("Game not found")
        val uid = game.turnPlayer.id
        if(uid != userId) throw Exception("Not your turn")

        val board = game.turnBoard
        val newBoard = board.placeShips(boatList)

        boardRepo.updateBoard(gameId, newBoard)

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
