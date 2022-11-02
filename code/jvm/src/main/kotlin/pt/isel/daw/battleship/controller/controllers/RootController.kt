package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.AppEndpointsMetaData
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.controller.hypermedia.siren.noEntitySiren
import pt.isel.daw.battleship.services.GeneralService
import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo

@RestController
class RootController(
    val generalService: GeneralService
) {
    @GetMapping(Uris.Home.ROOT)
    fun getHomeInfo() = noEntitySiren(AppEndpointsMetaData.root)

    @GetMapping(Uris.Home.SYSTEM_INFO)
    fun getSystemInfo(): SirenEntity<SystemInfo>{
        val sysInfo = generalService.getSystemInfo()
        return sysInfo.toSiren(AppEndpointsMetaData.systemInfo)
    }

    @GetMapping(Uris.Home.STATISTICS)
    fun getStatistics(): SirenEntity<GameStatistics> {
        val statistics = generalService.getStatistics()
        return statistics.toSiren(AppEndpointsMetaData.statistics)
    }
}
