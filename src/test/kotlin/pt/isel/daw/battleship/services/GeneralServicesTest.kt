package pt.isel.daw.battleship.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.repository.testWithTransactionManagerAndRollback

class GeneralServicesTest {

    @Test
    fun `getSystemInfo returns the correct system info`() {
        testWithTransactionManagerAndRollback {
            val generalService = GeneralService(it)

            val systemInfo = generalService.getSystemInfo()
            assertEquals(systemInfo.authors.sortedBy { it.name }.map { it.name },
                    listOf(
                    "Francisco Costa","Teodosie Pienescu","Tiago Filipe"
                    )
            )
        }
    }

    @Test
    fun `get statistics returns the correct statistics`() {
        testWithTransactionManagerAndRollback {
            val generalService = GeneralService(it)

            val statistics = generalService.getStatistics()

            assertEquals(statistics.nGames, 0)
            assertEquals(statistics.ranking.size, 0)
        }
    }


}