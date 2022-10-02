package pt.isel.daw.battleship.data

typealias Id = Int;

data class Square(val row: Row, val column: Column)
data class Row(val ordinal: Int){
    operator fun minus(other: Row): Int = ordinal - other.ordinal

}
data class Column(val ordinal: Int){
    operator fun minus(other: Column): Int = ordinal - other.ordinal

}

val Int.row get() = Row(this)
val Int.column get() = Column(this)



