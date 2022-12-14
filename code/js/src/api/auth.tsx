

function isLoggedIn() {
    return document.cookie.includes(COOKIE_NAME)
}

/**
 * Sets a past expire date so that the cookie is cleaned.
 */
function removeAuthCookie() {
    document.cookie = `${COOKIE_NAME}= ; expires = Thu, 01 Jan 1970 00:00:00 GMT`
}


function logout() {
    removeAuthCookie()
}

export const authServices ={
    logout,
    isLoggedIn
}

const COOKIE_NAME = "Authorization"