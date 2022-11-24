package pt.isel.daw.battleship.services.entities

data class SystemInfo(val authors : List<Author>, val version : String){
    data class Author(val name : String, val email : String, val github : String)
}