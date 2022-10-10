package pt.isel.daw.battleship.domain

sealed class BattleshipException(val code : Int, message : String) : Exception(message)

class GameNotFoundException(gameId : Int) : BattleshipException(1000, "Game with id: $gameId not found")
class Illegal

