package pt.isel.daw.battleship.data

import pt.isel.daw.battleship.data.model.Game

class FakeGameRepo: GameRepository {

    private val table = mutableMapOf<Id, Game>()

    override fun getGames(): List<Game> = table.values.toList()

    fun addGame(game: Game){
        table[game.Id] = game
    }

    fun hasGame(gameId: Id) = table[gameId] != null

}