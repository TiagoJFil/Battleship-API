

export interface ISystemInfoDTO {
    authors: Array<IAuthorDTO>,
    version: string
}

export interface IAuthorDTO {
    name: string,
    email: string,
    github: string,
    iselID: number
}


