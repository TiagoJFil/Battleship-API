package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.AppSirenNavigation
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.appToSiren
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.services.GeneralService
import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo

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
    fun getStatistics(): SirenEntity<GameStatistics> {
        val statistics = generalService.getStatistics()
        return statistics.appToSiren(AppSirenNavigation.STATISTICS_NODE_KEY)
    }

}
