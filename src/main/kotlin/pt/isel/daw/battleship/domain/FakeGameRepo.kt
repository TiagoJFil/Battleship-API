package pt.isel.daw.battleship.domain

import pt.isel.daw.battleship.domain.model.Game

class FakeGameRepo: GameRepository {

    private val table = mutableMapOf<Id, Game>()
    override fun getGameState(gameId: Id): Pair<Game.State, Id?> {
        TODO("Not yet implemented")
    }

    override fun getGames(): List<Game> = table.values.toList()

    fun addGame(game: Game){
        table[game.Id] = game
    }

    override fun hasGame(gameId: Id) = table[gameId] != null
    override fun verifyTurn(userId: Id, gameId: Id): Boolean {
        TODO("Not yet implemented")
    }

    override fun getGame(gameId: Id): Game? = table[gameId]
    override fun updateGame(gameId: Id, game: Game) {
        table[gameId] = game
    }

}