package pt.isel.daw.battleship.controller.hypermedia.siren.siren_navigation.builders

import pt.isel.daw.battleship.controller.hypermedia.siren.SirenAction


class SirenActionBuilder {

    private val fields = mutableListOf<SirenAction.Field>()

    fun textField(name: String, title: String? = null, value: String? = null) {
        field(name=name, type="text", title=title, value=value)
    }

    fun numberField(name: String, title: String? = null, value: String? = null) {
        field(name=name, type="number", title=title, value=value)
    }

    fun hiddenField(name: String, value: String, title: String? = null) {
        field(name=name, type="hidden", title=title, value=value)
    }

    fun field(
        name: String,
        type: String,
        value: String? = null,
        title: String? = null,
    ) = fields.add(SirenAction.Field(name, type, value, title))

    fun build(
        name: String,
        href: String,
        method: String,
        clazz: List<String>? = null,
        title: String? = null,
        type: String? = null
    ) = SirenAction(name=name, href=href, method=method, clazz=clazz, title=title, type=type, fields=fields)

}