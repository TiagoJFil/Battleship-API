//data class User(val name : String)

export interface IUserDTO {
    name: string
}

export interface IUserInfoInputModel {
    username: string,
    password: string,
}

export interface IAuthInformation {
    uid: UserID,
    token: string
}

export type UserID = number