

export interface SystemInfoDTO {
    authors: Array<AuthorDTO>,
    version: string
}

export interface AuthorDTO {
    name: string,
    email: string,
    github: string
}


