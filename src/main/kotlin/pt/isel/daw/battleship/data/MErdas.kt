package pt.isel.daw.battleship.data


data class User(val id: Id, val name: String)

data class Player(val attackBoard: Board, val defenseBoard: Board, val game: Id, val user: Id)



typealias Id = Int;

enum class ShipType(val size: Int){
    Carrier(5),
    Battleship(4),
    Cruiser(3),
    Submarine(3),
    Destroyer(2)
}


data class Square(val row: Int, val column: Char)



