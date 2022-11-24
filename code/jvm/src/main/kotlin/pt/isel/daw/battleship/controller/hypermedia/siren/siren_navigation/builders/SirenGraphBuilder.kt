package pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders

import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNavGraph

object NoEntitySiren

/**
 * Used to build a [SirenNavGraph].
 */
class SirenGraphBuilder{

    private val nodes = hashMapOf<String, SirenNode<*>>()


    fun <T> node(nodeID: String, builderScope: SirenNodeBuilder<T>.() -> Unit={}) {
        val node = with(SirenNodeBuilder<T>(nodeID)) {
            builderScope()
            build()
        }

        nodes[nodeID] = node
    }

    fun build(): SirenNavGraph = SirenNavGraph(nodes)

}