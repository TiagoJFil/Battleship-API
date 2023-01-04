export interface Relation{
    key: string,
    href: string,
    method: string
}

export const storage = new Map<string, Relation>();

export const RelationStorage = {
    addRelation: (relation: Relation) => {
        storage.set(relation.key, relation);
    },

    getRelation: (key: string) => {
        return storage.get(key);
    }
}

