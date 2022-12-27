package pt.isel.daw.battleship.siren_navigation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pt.isel.daw.battleship.controller.hypermedia.siren.*
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNavGraph

class SirenGraphUnitTests {


    @Test
    fun `toSiren usage in a simple SirenGraph`() {

        val simpleSirenGraph = SirenNavGraph {

            node<SportsList>("sports") {
                link(listOf("self"), "/api/sports")
            }

        }

        val sportsList = SportsList(
            listOf(Sport(1, "Football"), Sport(2, "Basketball")) // 2 sports
        )

        val sirenSportsListEntity: SirenEntity<SportsList> = sportsList.toSiren(simpleSirenGraph, "sports")

        val expectedSirenSportsEntity = SirenEntity(
            clazz = listOf("sports"),
            properties = sportsList,
            links = listOf(
                selfLink("/api/sports"),
            ),
        )

        assertEquals(expectedSirenSportsEntity, sirenSportsListEntity)
    }

    @Test
    fun `toSiren inserts the placeholders automatically in the uri based on the objects properties`(){

        val sportGraph = SirenNavGraph {
            node<Sport>("single-sport") {
                link(listOf("self"), "/api/sports/{id}")
            }
        }

        val football = Sport(id=1, name="Football")

        val sirenFootballEntity: SirenEntity<Sport> = football.toSiren(sportGraph, "single-sport")

        val expectedSirenFootballEntity = SirenEntity(
            clazz = listOf("single-sport"),
            properties = football,
            links = listOf(
                selfLink("/api/sports/1"), // the id is automatically inserted into the href
                                               // Replacing the {id} placeholder with the id property of the Sport object
            ),
        )

        assertEquals(expectedSirenFootballEntity, sirenFootballEntity)

    }

    data class RankedSport(val id: Int, val name: String, val rank: Int)
    data class RankedSportsList(val sports: List<RankedSport>)

    @Test
    fun `toSiren inserts the extra place holders`(){

        val sportsGraph = SirenNavGraph{

            node<RankedSportsList>("sports") {
                link(listOf("self"), "/api/sports")
                embeddedLink(rel=listOf("most-popular"), href="/api/sports/{mostPopularId}")
                // the mostPopularID is not a property of the RankedSportsList object
            }

            node<RankedSport>("single-sport") {
                link(listOf("self"), "/api/sports/{id}")
            }
        }

        val sportList = RankedSportsList(sports= listOf(
            RankedSport(id=1, name="Football", rank=1),
            RankedSport(id=2, name="Basketball", rank=2)
        ))

        // toSiren will filter out the most-popular link because the mostPopularID is not a property of the SportsList object
        // the placeholder can't be inserted
        val sirenEntity = sportList.toSiren(sportsGraph, "sports")

        val expectedSirenEntity = SirenEntity(
            clazz = listOf("sports"),
            properties = sportList,
            links = listOf(
                selfLink("/api/sports")
            )
        )

        assertEquals(expectedSirenEntity, sirenEntity)

        // To include the mostPopularID in the SirenEntity, we need to pass it as an extra placeholder

        val mostPopularSport = sportList.sports.minByOrNull { it.rank }?.id

        val sirenEntityWithExtraPlaceholders = sportList.toSiren(
            sirenGraph=sportsGraph,
            nodeID="sports",
            extraPlaceholders=mapOf("mostPopularId" to mostPopularSport?.toString())
        )

        val expectedEntityWithPlaceholder = SirenEntity(
            clazz = listOf("sports"),
            properties = sportList,
            links = listOf(
                selfLink("/api/sports")
            ),
            entities = listOf(
                EmbeddedLink( // The most popular sport is now shown as an embedded link
                    rel = listOf("most-popular"),
                    href = "/api/sports/1" // the mostPopularID is now inserted into the href
                )
            )
        )

        assertEquals(expectedEntityWithPlaceholder, sirenEntityWithExtraPlaceholders)

    }

    enum class ListState{
        EMPTY, FULL
    }

    data class StatefulSportsList(val sports: List<Sport>, val state: ListState)

    @Test
    fun `Relationships are only shown if they satisfy the showWhen predicate`(){

        val sirenGraph = SirenNavGraph{
            node<StatefulSportsList>("sports") {
                link(listOf("self"), "/api/sports")
                action("add-sport", "/api/sports", method = "POST") showWhen { it.state != ListState.FULL }
            }
        }

        val emptySportsList = StatefulSportsList(sports = emptyList(), state = ListState.EMPTY)

        val sirenEntity = emptySportsList.toSiren(sirenGraph, "sports")

        val expectedSirenWithAction = SirenEntity(
            clazz = listOf("sports"),
            properties = emptySportsList,
            links = listOf(
                selfLink("/api/sports")
            ),
            actions = listOf(
                SirenAction(
                    name = "add-sport",
                    href = "/api/sports",
                    method = "POST"
                )
            )
        )

        assertEquals(expectedSirenWithAction, sirenEntity)

        val fullSportsList = StatefulSportsList(sports = emptyList(), state = ListState.FULL)

        val fullSirenEntity = fullSportsList.toSiren(sirenGraph, "sports")

        val expectedSirenWithoutAction = SirenEntity(
            clazz = listOf("sports"),
            properties = fullSportsList,
            links = listOf(
                selfLink("/api/sports")
            )
        )

        assertEquals(expectedSirenWithoutAction, fullSirenEntity)

    }

    @Test
    fun `Placeholder insert prioritizes the object property over the extraPlaceholders`(){

        val sirenGraph = SirenNavGraph{
            node<Sport>("single-sport") {
                link(listOf("self"), "/api/sports/{id}")
                link(listOf("related"), "/api/sports/{id}")
            }
        }

        val football = Sport(id=1, name="Football")

        val sirenEntity = football.toSiren(
            sirenGraph=sirenGraph,
            nodeID="single-sport",
            extraPlaceholders=mapOf("id" to 2.toString())
        )

        val expectedSirenEntity = SirenEntity(
            clazz = listOf("single-sport"),
            properties = football,
            links = listOf(
                selfLink("/api/sports/1"),
                SirenLink(listOf("related"), "/api/sports/1")
            )
        )

        assertEquals(expectedSirenEntity, sirenEntity)

    }

    @Test
    fun `Placeholder with object property and extra placeholder`(){

        val sirenGraph = SirenNavGraph{
            node<Sport>("single-sport") {
                link(listOf("self"), "/api/sports/{id}")
                link(listOf("related"), "/api/sports/{relatedSportID}")
            }
        }

        val football = Sport(id=1, name="Football")

        val sirenEntity = football.toSiren(
            sirenGraph=sirenGraph,
            nodeID="single-sport",
            extraPlaceholders=mapOf("relatedSportID" to 2.toString())
        )

        val expectedSirenEntity = SirenEntity(
            clazz = listOf("single-sport"),
            properties = football,
            links = listOf(
                selfLink("/api/sports/1"),
                SirenLink(listOf("related"), "/api/sports/2")
            )
        )

        assertEquals(expectedSirenEntity, sirenEntity)

    }





}