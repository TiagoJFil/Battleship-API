package pt.isel.daw.battleship.controller.hypermedia.siren

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

data class SirenInfo(
    val name: String,
    val href: String,
    val method: String? = null,
    val outContentType: String? = null,

    val inContentType: String? = null,
    val fields: List<SirenAction.Field>? = null,
    val rel: List<String> = emptyList(),
    val title: String
) {

    fun toLink() = SirenLink(
        rel = rel,
        href = href,
        type = outContentType,
        title = title
    )


    fun toAction() = SirenAction(
        name = name,
        href = href,
        method = method,
        type = inContentType,
        fields = fields
    )


}