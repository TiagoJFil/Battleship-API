package pt.isel.daw.battleship.repository.dto

data class SystemInfoDTO(
    val name: String,
    val version: String,
)

data class SystemInfo(
    val name: String,
    val version: String,
    val authors: List<AuthorsDTO>
)

fun SystemInfoDTO.toSystemInfo(authors: List<AuthorsDTO>): SystemInfo {
    return SystemInfo(
        name = name,
        version = version,
        authors = authors
    )
}