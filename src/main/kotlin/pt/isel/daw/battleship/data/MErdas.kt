package pt.isel.daw.battleship.data

typealias Id = Int;

data class Square(val row: Row, val column: Column)
data class Row(val ordinal: Int){
    operator fun minus(other: Row): Int = ordinal - other.ordinal
    init {
        require(ordinal >= 0) { "Row must be positive" }
    }

}
data class Column(val ordinal: Int){
    operator fun minus(other: Column): Int = ordinal - other.ordinal

    init {
        require(ordinal >= 0) { "Column must be positive" }
    }
}

val Int.row get() = Row(this)
val Int.column get() = Column(this)



