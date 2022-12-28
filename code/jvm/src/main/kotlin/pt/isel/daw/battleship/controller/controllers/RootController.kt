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
    fun getSystemInfo(request: HttpServletRequest): SirenEntity<SystemInfo>{
        val sysInfo = generalService.getSystemInfo()
        val node =
            if(request.cookies.isNotEmpty())
                AppSirenNavigation.SYSTEM_INFO_NODE_KEY_WITH_AUTH
            else
                AppSirenNavigation.SYSTEM_INFO_NODE_KEY
        return sysInfo.appToSiren(node)
    }

    @GetMapping(Uris.Home.STATISTICS)
    fun getStatistics(@RequestParam(required = false) embedded : Boolean , request: HttpServletRequest): SirenEntity<Statistics> {
        val embeddableStatistics = generalService.getStatistics(embedded)
        val node = if(request.cookies.isNotEmpty()) AppSirenNavigation.STATISTICS_NODE_KEY_WITH_AUTH else AppSirenNavigation.STATISTICS_NODE_KEY
        val statsSiren = embeddableStatistics.statistics.appToSiren(node)

        if(embedded) {
            val safeUsers = requireNotNull(embeddableStatistics.users)
            val siren = safeUsers.foldIndexed(statsSiren) { idx, acc, user ->
                val stat = embeddableStatistics.statistics.ranking[idx]
                val result = acc.appAppendEmbedded(
                    AppSirenNavigation.USER_NODE_KEY,
                    user,
                    AppSirenNavigation.STATISTICS_NODE_KEY,
                    mapOf("userID" to stat.playerID.toString())
                )
                result
            }
            return siren
        }

        return statsSiren
    }

}
