package pt.isel.daw.battleship.controller.hypermedia.siren

data class SirenInfo(
    val name: String,
    val href: String,
    val method: String? = null,
    val type: String? = null,
    val fields: List<SirenAction.FieldType>? = null,
    val rel: List<String> = emptyList(),
    val title: String
) {
    fun toLink() = SirenLink(
        rel = rel,
        href = href,
        type = type,
        title = title
    )

    fun toAction() = SirenAction(
        name = name,
        href = href,
        method = method,
        type = type,
        fields = fields
    )
}