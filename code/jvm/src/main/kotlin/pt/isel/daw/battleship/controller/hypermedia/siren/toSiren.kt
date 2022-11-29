package pt.isel.daw.battleship.controller.hypermedia.siren

import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNavGraph
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.SirenNodeID
import pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

//TODO : remove this
data class EmbeddedEntityInfo<E>(
    val nodeID : String,
    val entity : E,
)

/**
 * Other interface to the [appendEmbedded] function. Using reified type parameters.
 * @see appendEmbedded
 */
inline fun <reified T: Any,reified E: Any> SirenEntity<T>.appendEmbedded(
    sirenGraph: SirenNavGraph,
    embeddedNodeID : String,
    entity : E,
    originalNodeID : SirenNodeID,
    extraPlaceholders: Map<String, String?>?
) : SirenEntity<T> =
    this.appendEmbedded(sirenGraph,embeddedNodeID,entity,T::class,E::class,originalNodeID,extraPlaceholders)

/**
 * Appends an embedded [E] entity to the current [T] entity.
 *
 * @param sirenGraph The [SirenNavGraph] to use to build the embedded entity.
 * @param embeddedNodeID The node ID of the embedded entity.
 * @param entity The entity to embed.
 * @param kClassE The [KClass] of the entity to embed.
 * @param kClassT The [KClass] of the current entity.
 * @param originalNodeID The [SirenNodeID] of the current entity.
 * @param extraPlaceholders The extra placeholders to use when building the embedded entity.
 *
 * @return the current [SirenEntity<T>] entity with the embedded [E] entity appended.
 */
fun <T : Any ,E : Any> SirenEntity<T>.appendEmbedded(
    sirenGraph: SirenNavGraph,
    embeddedNodeID : String,
    entity : E,
    kClassT: KClass<T>,
    kClassE: KClass<E>,
    originalNodeID : SirenNodeID,
    extraPlaceholders: Map<String, String?>? = null
) : SirenEntity<T> {
    val embeddedNode = sirenGraph[embeddedNodeID] as SirenNode<E>

    val placeholdersT =this.properties?.getPlaceholders(kClassT) ?: emptyMap()
    val placeholdersE = entity.getPlaceholders(kClassE)
    val placeholders = placeholdersT + placeholdersE + (extraPlaceholders ?: emptyMap())

    val originalNode = sirenGraph[originalNodeID] as SirenNode<T>  // use this or use originalNodeID on the argument

    val rel = originalNode.embeddedEntitiesRel?.firstOrNull { it.kClass == kClassE }?.entity?.rel ?: throw  NoSuchElementException("No relation found between ${kClassE.simpleName} and ${kClassT.simpleName}")

    val embeddedEntity = embeddedNode.toSirenEmbeddedEntity(entity,rel, placeholders)

    return this.copy(
        entities = this.entities?.plus(embeddedEntity)  ?: listOf(embeddedEntity)
    )
}

/**
 * Other interface to the [toSirenEntity] function. Using reified type parameters.
 * @see toSirenEntity
 */
inline fun <reified T : Any> T.toSiren(
    sirenGraph: SirenNavGraph,
    nodeID: SirenNodeID,
    extraPlaceholders: Map<String, String?>? = null
): SirenEntity<T> =
    toSirenEntity(sirenGraph, nodeID, T::class, extraPlaceholders)

/**
 * Returns a SirenEntity for the given object.
 *
 * Make sure the node identified by nodeID is in the sirenGraph declared with the same type as the object being converted.
 *
 * This function tries to replace all the placeholders in the hrefs of the relations of the node identified by [nodeID] in the graph.
 * If the placeholder is not found in the object's properties, it will look in the [extraPlaceholders] map.
 * If the placeholder is not found in either of the two places, it will filter the relation out so it is not included in the SirenEntity.
 *
 * This function will also filter out the relations that don't satisfy their respective showWhen predicates.
 *
 * @param sirenGraph The SirenNavGraph that contains the SirenNode that describes the entity
 * @param nodeID The ID of the SirenNode that describes the entity
 * @param kClass [T] metadata
 * @param extraPlaceholders A map of extra placeholders to replace in the hrefs of the relationships
 */
fun <T : Any> T.toSirenEntity(
    sirenGraph: SirenNavGraph,
    nodeID: SirenNodeID,
    kClass: KClass<T>,
    extraPlaceholders: Map<String, String?>? = null
): SirenEntity<T> {

    val node = sirenGraph[nodeID] as SirenNode<T>

    val placeholders = this.getPlaceholders(kClass)

    // Remove duplicate keys prioritizing the placeholders from the object properties
    val remainingPlaceholders = extraPlaceholders?.filter { !placeholders.containsKey(it.key) }
    val allPlaceholders = placeholders + remainingPlaceholders.orEmpty() // merge the two maps

    return node.toEntity(this, allPlaceholders)
}


fun noEntitySiren(
    navGraph: SirenNavGraph,
    nodeID: SirenNodeID,
    placeholders: Map<String, String?> = emptyMap()
): SirenEntity<NoEntitySiren> {
    val node = (navGraph[nodeID] as SirenNode<NoEntitySiren>)

    return node.toEntity(NoEntitySiren, placeholders)
}


/**
 * Replaces all the empty collections in this SirenEntity object with null.
 */
private fun <T> SirenEntity<T>.nullifyEmptyCollections() = copy(
        links = links?.ifEmpty { null },
        actions = actions
            ?.ifEmpty { null }
            ?.map {
                it.copy(fields = it.fields?.ifEmpty { null })
            },
        entities = entities?.ifEmpty { null }
    )

/**
 * Tries to fill the URI placeholders with the properties of the object.
 *
 * It matches the placeholder with the property name, and replaces it with the property value.
 *
 * Removes the relation if the placeholder is not found in the placeholders map.
 */
private fun <T : Any> SirenEntity<T>.tryExpandHrefs(placeholders: Map<String, String?>): SirenEntity<T> {

    val newLinks = this.links?.mapNotNull { link ->
        val newHref = link.href.replacePlaceholders(placeholders)
        newHref?.let { link.copy(href = newHref) }
    }

    val newActions = this.actions?.mapNotNull { action ->
        val newHref = action.href.replacePlaceholders(placeholders)
        newHref?.let { action.copy(href = newHref) }
    }

    val newEntities = this.entities?.mapNotNull { subEntity ->
        if (subEntity is EmbeddedLink) {
            val newHref = subEntity.href.replacePlaceholders(placeholders) ?: return@mapNotNull null
            subEntity.copy(href = newHref)
        } else {
            subEntity
        }
    }

    return copy(
        links = newLinks,
        actions = newActions,
        entities = newEntities
    )
}

/**
 * Replaces all placeholders in the given string with the values from the given map if
 * all the placeholder keys are present in the map.
 * Unusable for strings that have '{' and '}' characters that are not placeholders.
 *
 *      String: "gf/{foo}/{bar}"
 *      placeholders: {foo: 1, bar: 2}
 *      result: gf/1/2
 *
 *      String: "gf/{foo}/{bar}"
 *      placeholders: {foo: 1}
 *      result: null
 *
 *
 * @param placeholders The map containing the values to replace the placeholders with.
 *
 * @return The string with all placeholders replaced with the values from the map.
 */
private fun String.replacePlaceholders(placeholders: Map<String, String?>): String? {
    if (!this.contains("{")) return this

    val placeholderStrings = placeholders
        .filter { it.value != null }
        .map { "{${it.key}}" }

    if (placeholderStrings.none { this.contains(it) }) return null

    return placeholders.entries
        .filter { it.value != null }
        .fold(this) { accHref, entry ->
            val (key, value) = entry
            val placeholder = "{$key}"
            if (value != null)
                accHref.replace(placeholder, value)
            else
                accHref
        }
}

/**
 * Given an object and kClass, returns a map of the object's properties and their values.
 * The map is used to replace the placeholders in the hrefs of the relations.
 * @param T the object
 * @param kClass[T] class information
 */
fun <R : Any> R.getPlaceholders(kClass : KClass<R>) =
    kClass.memberProperties
        .associate { prop -> prop.name to prop.getter.call(this)?.toString() }

/**
 * Filters all the relationships that don't satisfy their respective showWhen predicates.
 */
private fun <T : Any> SirenNode<T>.filterRelationshipsByPredicate(instance: T): SirenNode<T> {

    val callPredicate: (Relationship<T>) -> Boolean = { it ->
        it.predicate?.invoke(instance) ?: true // if no predicate, always show
    }

    return this.copy(
        links = this.links?.filter(callPredicate),
        actions = this.actions?.filter(callPredicate),
        entities = this.entities?.filter(callPredicate)
    )

}

/**
 * Transforms a SirenNode into a SirenEntity.
 *
 * First it filters the relationships that don't satisfy their respective showWhen predicates.
 * Then it replaces all the placeholders in the hrefs of the relationships.
 *
 * @param instance The object that will be used to fill the entity's properties.
 * @param placeholders A map of placeholders to expand the hrefs of the relationships.
 * @return A SirenEntity with the properties and relationships of the SirenNode.
 */
private fun <T : Any> SirenNode<T>.toEntity(
    instance: T,
    placeholders: Map<String, String?> = emptyMap()
): SirenEntity<T> {
    val newNode = this.filterRelationshipsByPredicate(instance)

    return SirenEntity(
        clazz = newNode.clazz,
        properties = instance,
        links = newNode.links?.map(LinkRelationship<T>::link),
        actions = newNode.actions?.map(ActionRelationship<T>::action),
        entities = newNode.entities?.map { it.link }
    ).tryExpandHrefs(placeholders)
        .nullifyEmptyCollections()
}

/**
 * Transforms a SirenNode into a SirenEntity.
 *
 * @param instance The object that will be used to fill the entity's properties.
 * @param placeholders A map of placeholders to expand the hrefs of the relationships.
 * @param rel The relation of the embedded entity.
 * @return A SirenEntity with the properties and relationships of the SirenNode.
 */
private fun <E: Any> SirenNode<E>.toSirenEmbeddedEntity(
    instance: E,
    rel : List<String>,
    placeholders: Map<String, String?>
) : EmbeddedEntity<E> {
    val sirenEntity = this.toEntity(instance,placeholders)

    return EmbeddedEntity<E>(
        rel = rel,
        clazz = sirenEntity.clazz,
        properties = sirenEntity.properties,
        entities  = sirenEntity.entities,
        links  = sirenEntity.links,
        actions  = sirenEntity.actions,
        title = sirenEntity.title
    )
}