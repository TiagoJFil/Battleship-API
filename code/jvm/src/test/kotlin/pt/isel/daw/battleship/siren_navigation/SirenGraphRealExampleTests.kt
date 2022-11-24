package pt.isel.daw.battleship.siren_navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.controller.hypermedia.siren.*
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNavGraph
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.NoEntitySiren


class SirenGraphRealExampleTests {

    companion object{
        const val ROOT_NODE = "root"
        const val SPORTS_NODE = "sports"
        const val ACTIVITIES_NODE = "activities"
        const val SINGLE_SPORT_NODE = "single-sport"
        const val SINGLE_ACTIVITY_NODE = "single-activity"
    }

    private val sirenTestGraph: SirenNavGraph = SirenNavGraph {
        node<NoEntitySiren>(ROOT_NODE) {
            self( "/api/")
            link(listOf("sports"), "/api/sports")
            link(listOf("activities"), "/api/activities")
        }

        node<SportsList>(SPORTS_NODE) {
            self( "/api/sports?page={currentPage}")
            link(listOf("next"), "/api/sports?page={nextPage}")
            link(listOf("prev"), "/api/sports?page={prevPage}")
            link(listOf("root"), "/api/")
        }

        node<Sport>(SINGLE_SPORT_NODE) {
            link(listOf("root"), "/api/")
            self( "/api/sports/{id}")
            link(listOf("activities"), "/api/sports/{id}/activities")
            action("edit", "/api/sports/{id}", "PUT", title = "Edit pt.isel.daw.battleship.siren_navigation.Sport") {
                field("name", type = "text")
            }
        }

        node<ActivitiesList>(ACTIVITIES_NODE) {
            self( "/api/activities?page={currentPage}")
            link(listOf("next"), "/api/activities?page={nextPage}")
            link(listOf("prev"), "/api/activities?page={prevPage}")
            action("create-activity", "/api/activities", "POST")

        }

        node<Activity>(SINGLE_ACTIVITY_NODE) {
            link(listOf("root"), "/api/")
            self( "/api/sports/{sportID}/activities/{id}") showWhen { it.sportID != null }
            link(listOf("self"), "/api/activities/{id}") showWhen { it.sportID == null }
            action("edit", "/api/activities/{id}", "PUT"){
                field("name", type = "text")
            }
            embeddedLink(rel=listOf("sport"), href="/api/sports/{sportID}", type=SirenContentType)
        }
    }


    @Test
    fun `to Siren returns the correct Siren representation of the sports node`(){

        val sport = Sport(1, "Running")
        val sirenEntity = sport.toSiren(sirenTestGraph, SINGLE_SPORT_NODE)

        val expectedEntity = SirenEntity(
            clazz = listOf(SINGLE_SPORT_NODE),
            properties = sport,
            links = listOf(
                SirenLink(rel = listOf("root"), href = "/api/"),
                selfLink("/api/sports/1"),
                SirenLink(rel = listOf("activities"), href = "/api/sports/1/activities"),
            ),
            actions = listOf(
                SirenAction(
                    name = "edit",
                    href = "/api/sports/1",
                    method = "PUT",
                    title = "Edit pt.isel.daw.battleship.siren_navigation.Sport",
                    fields = listOf(
                        SirenAction.Field(name = "name", type = "text")
                    )
                )
            )
        )

        assertEquals(expectedEntity, sirenEntity)
    }

    @Test
    fun `sport related links only show when predicate is true for activities`(){
        val activity = Activity(1, "Running", 1)
        val sirenEntity = activity.toSiren(sirenTestGraph, SINGLE_ACTIVITY_NODE)

        val expectedEntity = SirenEntity(
            clazz = listOf(SINGLE_ACTIVITY_NODE),
            properties = activity,
            links = listOf(
                SirenLink(rel = listOf("root"), href = "/api/"),
                selfLink("/api/sports/1/activities/1"),
            ),
            actions = listOf(
                SirenAction(
                    name = "edit",
                    href = "/api/activities/1",
                    method = "PUT",
                    fields = listOf(
                        SirenAction.Field(name = "name", type = "text")
                    )
                )
            ),
            entities= listOf(
                EmbeddedLink(
                    rel = listOf("sport"),
                    href = "/api/sports/1",
                    type = SirenContentType
                )
            )
        )

        assertEquals(expectedEntity, sirenEntity)
    }


}