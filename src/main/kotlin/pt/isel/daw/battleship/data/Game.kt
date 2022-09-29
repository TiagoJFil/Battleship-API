package pt.isel.daw.battleship.data



enum class GameState{
    WAITING_PLAYER, //??? , when waiting player ainda n vai existir game 
    PLACING_SHIPS,
    PLAYING,
    FINISHED
}

data class GameRules(
    val nShots : Int,
    val nTiles : Int
)

data class Game(
    val Id: Id,
    val state: GameState,
    val player1: Player,
    val player2: Player,
    val rules: GameRules
){

    




}