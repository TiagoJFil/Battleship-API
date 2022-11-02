package pt.isel.daw.battleship.controller.hypermedia.siren

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

data class SirenInfo(
    val name: String,
    val href: String,
    val method: String? = null,
    val outContentType: String? = null,

    val inContentType: String? = null,
    val fields: List<SirenAction.FieldType>? = null,
    val rel: List<String> = emptyList(),
    val title: String
) {

    fun toLink() = SirenLink(
        rel = rel,
        href = href,
        type = outContentType,
        title = title
    )

    private val defaultFields = fields?.map { field ->
        when(field) {
            is SirenAction.Field ->  field
            is SirenAction.ListField<*> -> SirenAction.Field(
                name = field.name,
                type = objectMapper.writeValueAsString(field.type)
                    .filter { it != '\\' && it != '"' }
                    .removeSurrounding("[", "]"),
                title = field.title,
                value = field.value
            )
        }
    }

    fun toAction() = SirenAction(
        name = name,
        href = href,
        method = method,
        type = inContentType,
        fields = defaultFields
    )


    companion object{
        private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    }
}