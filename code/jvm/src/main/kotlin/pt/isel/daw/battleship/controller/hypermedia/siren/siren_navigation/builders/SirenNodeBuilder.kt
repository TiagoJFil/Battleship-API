package pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders

import pt.isel.daw.battleship.controller.hypermedia.siren.*
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNodeID
import kotlin.reflect.KClass

abstract class Relationship<T> {
    var predicate: ((T) -> Boolean)? = null
}

data class LinkRelationship<T>(val link: SirenLink) : Relationship<T>()
data class ActionRelationship<T>(val action: SirenAction) : Relationship<T>()
data class EmbeddedEntityRelationship<T: Any>(val entity: EmbeddedEntity<T>, val kClass : KClass<T> ) : Relationship<T>()
data class EmbeddedLinkRelationship<T>(val link: EmbeddedLink) : Relationship<T>()


data class SirenNode<T>(
    val clazz: List<String>? = null,
    val properties: T? = null,
    val entities: List<EmbeddedLinkRelationship<T>>? = null,
    val links: List<LinkRelationship<T>>? = null,
    val actions: List<ActionRelationship<T>>? = null,
    val title: String? = null,
    val embeddedEntitiesRel : List<EmbeddedEntityRelationship<*>>? = null
)

/**
 * Builds a [SirenNode] for the SirenNavGraph.
 */
class SirenNodeBuilder<T>(val id: SirenNodeID) {

    private val links = mutableListOf<LinkRelationship<T>>()
    private val actions = mutableListOf<ActionRelationship<T>>()
    private val embeddedLinks = mutableListOf<EmbeddedLinkRelationship<T>>()
    private val embeddedEntitiesRel = mutableListOf<EmbeddedEntityRelationship<*>>()

    infix fun Relationship<T>.showWhen(predicate: (T) -> Boolean) {
        this.predicate = predicate
    }

    fun link(rel: List<String>, href: String, title: String? = null, type: String? = null): Relationship<T> {
        val link = SirenLink(rel, href, title, type)
        return LinkRelationship<T>(link).also { links.add(it) }
    }

    fun self(href: String, title: String? = null, type: String? = null): Relationship<T> {
        return link(rel = listOf("self"), href = href, title = title, type = type)
    }

    fun action(
        name: String,
        href: String,
        method: String = "GET",
        clazz: List<String>? = null,
        title: String? = null,
        type: String? = null,
        builderScope: SirenActionBuilder.() -> Unit = {}
    ): Relationship<T> {
        val builder = SirenActionBuilder()

        builder.builderScope()
        val sirenAction =
            builder.build(name = name, href = href, method = method, clazz = clazz, title = title, type = type)

        return ActionRelationship<T>(sirenAction).also {
            actions.add(it)
        }
    }

    fun embeddedLink(
        clazz: List<String>? = null,
        title: String? = null,
        rel: List<String>,
        href: String,
        type: String? = null
    ): Relationship<T> {
        val embeddedLink = EmbeddedLink(clazz, rel, href, type, title)
        return EmbeddedLinkRelationship<T>(embeddedLink).also {
            embeddedLinks.add(it)
        }
    }

    inline fun <reified E: Any>embeddedEntity(
        rel: List<String>
    ): Relationship<E> {
        return embeddedEntity(rel, E::class)
    }
    fun <E: Any> embeddedEntity(
        rel: List<String>,
        kClass: KClass<E>
    ): Relationship<E> {
        val embeddedEntity = EmbeddedEntity<E>(rel)
        return EmbeddedEntityRelationship(embeddedEntity, kClass).also {
            embeddedEntitiesRel.add(it)
        }
    }

    fun build() = SirenNode(
        clazz = listOf(id),
        links = links.ifEmpty { null },
        actions = actions.ifEmpty { null },
        entities = embeddedLinks.ifEmpty { null },
        embeddedEntitiesRel = embeddedEntitiesRel.ifEmpty { null }
    )

}