import { AuthInformation } from "../entities/user";


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
    uid: 2,
    token: '08807b44-45ec-45ac-bfc7-f9d8e2ec67d9'
}

export function isLoggedIn(): boolean {
    return getAuthInfo() !== null
}

export function logout() {
    sessionStorage.removeItem('authInfo')
}