package pt.isel.daw.battleship.services

import org.springframework.stereotype.Component
import pt.isel.daw.battleship.data.GameRepository
import pt.isel.daw.battleship.data.Id
import pt.isel.daw.battleship.data.Tile


class GameService(
    val repository: GameRepository
) {

    val board1 = "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########"

    val board2 = "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########" +
                 "#########"

    //Allow an user to define a set of shots on each round.
    fun makeShoot(tiles: List<Tile>, userId: Id, gameId: Id){
//list because it depends on the number of shots of the game

        // val game = repo.getGame(gameId)
        // Match User
        // ver se a tile é valida
        // game.makeMove(tile)
        // repo.updateGame(game)
    }

    //Allow an user to define the layout of their fleet in the grid.
    fun setBoardLayout(layout: String, userId: Id, gameId : Id){

        // get game from repo
        //verify if layout is valid (Size,turn , then usual rules)

        // game.setBoardLayout(layout)
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