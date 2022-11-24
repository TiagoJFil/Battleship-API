package pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation

import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.SirenGraphBuilder
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.SirenNode


typealias SirenNodeID = String

inline fun buildSirenGraph(builderScope: SirenGraphBuilder.() -> Unit): SirenNavGraph {
    return with(SirenGraphBuilder()) {
        builderScope()
        build()
    }
}

/**
 * Represents a Siren Navigation Graph.
 *
 * Serves as a meta-data container for an API.
 *
 * Used along with toSiren function to generate a Siren representation of an object.
 */
class SirenNavGraph(private val nodes: Map<SirenNodeID, SirenNode<*>>) {

    /**
     * Returns the [SirenNode] associated with the given [nodeID] ensuring it exists.
     *
     * @throws NoSuchElementException if no node is associated with the given [nodeID].
     */
    operator fun get(nodeID: SirenNodeID): SirenNode<*> = nodes[nodeID]
        ?: throw NoSuchElementException("Siren Node $nodeID not found in this graph")

    companion object{
        inline operator fun invoke(builderScope: SirenGraphBuilder.() -> Unit): SirenNavGraph
            = buildSirenGraph(builderScope)
    }
}


