import { AuthInformation } from "../interfaces/entities/user";


//temporarly using session but will be replaced by cookies


export function setAuthInfo(authInfo: AuthInformation) {
    sessionStorage.setItem('authInfo', JSON.stringify(authInfo))
}

export function getAuthInfo(): AuthInformation | null {
    const authInfo = sessionStorage.getItem('authInfo')
    return fakeAuth
    if (authInfo) {
        return JSON.parse(authInfo)
    }
    return null
}

const fakeAuth = {
    uid: 1,
    token: 'c527ca81-e264-4be2-a32c-4e735f758879'
}

export function isLoggedIn(): boolean {
    return getAuthInfo() !== null
}

export function logout() {
    sessionStorage.removeItem('authInfo')
}