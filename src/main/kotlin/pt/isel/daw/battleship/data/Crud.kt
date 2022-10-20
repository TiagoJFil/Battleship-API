package pt.isel.daw.battleship.data

interface Crud<T, Tid> {

    fun create(thing: T): Tid
    fun read(id: Tid): T?
    fun update(thing: T)
    fun delete(id: Tid)

}