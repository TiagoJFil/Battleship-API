package pt.isel.daw.battleship.controller

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.GameService

@Component
class CancelGamesScheduler(
    private val gameService: GameService
) {

    companion object{
        const val CANCEL_DELAY: Long = 1000 * 60
    }

    @Scheduled(fixedRate = CANCEL_DELAY)
    fun cancelGames(){
        gameService.cancelTimedOutGames()
    }

}