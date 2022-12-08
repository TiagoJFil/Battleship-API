//data class User(val name : String)

export interface UserDTO {
    name: string
}

export interface UserInfoInputModel {
    username: string,
    password: string,
}

export interface AuthInformation {
    uid: UserID,
    token: string
}

export type UserID = number