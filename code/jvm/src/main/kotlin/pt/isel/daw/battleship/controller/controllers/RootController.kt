package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.*
import pt.isel.daw.battleship.services.GeneralService
import pt.isel.daw.battleship.services.entities.Statistics
import pt.isel.daw.battleship.services.entities.SystemInfo
import javax.servlet.http.HttpServletRequest

@RestController
class RootController(
    val generalService: GeneralService
) {

    @GetMapping(Uris.Home.ROOT)
    fun getHomeInfo() = noEntitySiren(AppSirenNavigation.graph, AppSirenNavigation.ROOT_NODE_KEY)

    @GetMapping(Uris.Home.SYSTEM_INFO)
    fun getSystemInfo(): SirenEntity<SystemInfo>{
        val sysInfo = generalService.getSystemInfo()

        return sysInfo.appToSiren(AppSirenNavigation.SYSTEM_INFO_NODE_KEY)
    }

    @GetMapping(Uris.Home.STATISTICS)
    fun getStatistics(@RequestParam(required = false) embedded : Boolean): SirenEntity<Statistics> {
        val embeddableStatistics = generalService.getStatistics(embedded)
        val statsSiren = embeddableStatistics.statistics.appToSiren(AppSirenNavigation.STATISTICS_NODE_KEY)

        if(embedded) {
            val safeUsers = requireNotNull(embeddableStatistics.users)
            val siren = safeUsers.foldIndexed(statsSiren) { idx, acc, user ->
                val stat = embeddableStatistics.statistics.ranking[idx]
                val result = acc.appAppendEmbedded(
                    AppSirenNavigation.STATISTICS_NODE_KEY,
                    user,
                    AppSirenNavigation.USER_NODE_KEY,
                    mapOf("userID" to stat.playerID.toString())
                )
                result
            }
            return siren
        }

        return statsSiren
    }
}
