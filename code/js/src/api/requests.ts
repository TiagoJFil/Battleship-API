import axios from "axios"
import { EmbeddedLink, SirenAction, SirenEntity, SirenLink, SubEntity } from "../interfaces/hypermedia/siren"
import { Relation, RelationStorage, storage } from "./relation-storage"

export async function sendRelationRequest(relation: Relation, data?: any, query?: Object) : Promise<SirenEntity<any>> {

    const response = await axios({
        url: buildUrl(relation.href, query), 
        method: relation.method,
        data: JSON.stringify(data)
    }).catchAndThrowAsProblem()



    fillRelationsFromEntity(response.data)



    return response.data
}

const actionToRelation = (action: SirenAction) => {
    return {
        key: action.name,
        href: action.href,
        method: action.method,
    }
}

const linkToRelation = (link: SirenLink) => {
    return {
        key: link.rel[0],
        href: link.href,
        method: 'GET',
    }
}

const embeddedLinkToRelation = (link: EmbeddedLink) => {
    return {
        key: link.rel[0],
        href: link.href,
        method: 'GET',
    }
}

export const fillRelationsFromEntity = (entity: SirenEntity<any>) => {
    const actionRelations = entity?.actions?.map((action: SirenAction) => actionToRelation(action)) ?? []
    const linkRelations = entity?.links
    ?.filter((link: SirenLink) => !link.rel.includes('self'))
    ?.map((link: SirenLink) => linkToRelation(link)) ?? []
    const embeddedEntities = entity?.entities?.map((entity: EmbeddedLink) => embeddedLinkToRelation(entity)) ?? []

    const relations = [...actionRelations, ...linkRelations, ...embeddedEntities]
    relations.forEach(relation => {
        RelationStorage.addRelation(relation)
    })
}

export const ensureRelation = async (relationKey: string, fillRelationsFunction?: () => Promise<void>) => {
    const relation = RelationStorage.getRelation(relationKey) 
    if(!relation){
        if(!fillRelationsFunction) throw new Error(`Couldn't ensure relation ${relationKey}. No fillRelationsFunction provided.`)
        await fillRelationsFunction()
        return RelationStorage.getRelation(relationKey)
    }



    return relation
}

const buildUrl = (href: string, query?: Object) => {
    if(query){
        const queryStrings = Object.keys(query).map(key => `${key}=${query[key]}`)
        const queryString = queryStrings.join('&')
        return `${href}?${queryString}`
    }

    return href
}