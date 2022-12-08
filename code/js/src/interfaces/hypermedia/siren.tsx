

export interface SirenEntity<T> {
    clazz?: Array<string>,
    properties?: T,
    entities?: Array<SubEntity>,
    links?: Array<SirenLink>,
    actions?: Array<SirenAction>,
    title?: string
}

export interface SirenLink {
    rel: Array<string>,
    href: string,
    title?: string,
    type?: string
}


export interface SirenAction {
    name: string,
    href: string,
    title?: string,
    class?: Array<string>,
    method?: string,
    type?: string,
    fields?: Array<Field>
}
export interface Field {
    name: string,
    type?: string,
    value?: string,
    title?: string,
}


export interface SubEntity{}
export interface EmbeddedEntity <T> extends SubEntity{
    rel: Array<string>,
    clazz?: Array<string>,
    properties?: T,
    entities?: Array<SubEntity>,
    links?: Array<SirenLink>,
    actions?: Array<SirenAction>,
    title?: string
}

export interface EmbeddedLink extends SubEntity {
    rel: Array<string>,
    clazz?: Array<string>,
    href: string,
    type?: string,
    title?: string
}
