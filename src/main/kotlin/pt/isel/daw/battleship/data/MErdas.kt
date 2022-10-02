package pt.isel.daw.battleship.data

typealias Id = Int;

data class Square(val row: Row, val column: Column)
data class Row(val ordinal: Int)
data class Column(val ordinal: Int)

val Int.row get() = Row(this)
val Int.column get() = Column(this)



