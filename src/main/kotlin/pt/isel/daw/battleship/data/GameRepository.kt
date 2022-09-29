package pt.isel.daw.battleship.data

import org.springframework.stereotype.Component

@Component
interface GameRepository {

    fun getGames(): List<Game>

    fun getWaitingForPlayerGames(): List<Game>



}