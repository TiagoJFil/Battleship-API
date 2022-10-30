package pt.isel.daw.battleship.controller.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.daw.battleship.controller.Method.*
import pt.isel.daw.battleship.controller.MethodInfo
import pt.isel.daw.battleship.controller.hypermedia.siren.toSiren
import pt.isel.daw.battleship.controller.Uris
import pt.isel.daw.battleship.controller.hypermedia.siren.SirenEntity
import pt.isel.daw.battleship.services.GeneralService
import pt.isel.daw.battleship.services.entities.GameStatistics
import pt.isel.daw.battleship.services.entities.SystemInfo

@RestController
class RootController(
    val generalService: GeneralService
) {
    @GetMapping(Uris.Home.SYSTEM_INFO)
    fun getSystemInfo(): SirenEntity<SystemInfo> {
        val sysInfo = generalService.getSystemInfo()
        return sysInfo.toSiren(MethodInfo(Uris.Home.SYSTEM_INFO, GET))
    }

    @GetMapping(Uris.Home.STATISTICS)
    fun getStatistics(): SirenEntity<GameStatistics> {
        val statistics = generalService.getStatistics()
        return statistics.toSiren(MethodInfo(Uris.Home.STATISTICS, GET))
    }
}
