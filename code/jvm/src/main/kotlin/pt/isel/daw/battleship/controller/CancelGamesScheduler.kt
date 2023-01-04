package pt.isel.daw.battleship.controller

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import pt.isel.daw.battleship.services.GameService

@Component
class CancelGamesScheduler(
    private val gameService: GameService
) {



    companion object{
        const val CANCEL_DELAY: Long = 1000 * 60 // 1 minute
        val logger = LoggerFactory.getLogger(CancelGamesScheduler::class.java)
    }

    @Scheduled(fixedRate = CANCEL_DELAY)
    fun cancelGames(){
        logger.info("Cancelling timed out games...")
        gameService.cancelTimedOutGames()
    }

}