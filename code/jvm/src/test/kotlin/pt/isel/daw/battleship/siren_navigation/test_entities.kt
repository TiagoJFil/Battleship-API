package pt.isel.daw.battleship.siren_navigation

data class SportsList(val sports: List<Sport>)
data class Sport(val id: Int, val name: String)

data class ActivitiesList(val activities: List<Activity>)
data class Activity(val id: Int, val name: String, val sportID: Int?)