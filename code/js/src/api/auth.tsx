function isLoggedIn() {
    return document.cookie.includes(AUTH_COOKIE_NAME)
}

/**
 * Sets a past expire date so that the cookie is cleaned.
 */
function removeAuthCookies() {
    document.cookie = `${AUTH_COOKIE_NAME}= ; expires = Thu, 01 Jan 1970 00:00:00 GMT`
    document.cookie = `${UID_COOKIE_NAME}= ; expires = Thu, 01 Jan 1970 00:00:00 GMT`
}

function logout() {
    removeAuthCookies()
}

export const authServices ={
    logout,
    isLoggedIn
}

export function getCookie(name: String) {
    // Split cookie string and get all individual name=value pairs in an array
    let cookieArr = document.cookie.split(";");

    // Loop through the array elements
    for(let i = 0; i < cookieArr.length; i++) {
        let cookiePair = cookieArr[i].split("=");
        
        /* Removing whitespace at the beginning of the cookie name
        and compare it with the given string */
        if(name == cookiePair[0].trim()) {
            // Decode the cookie value and return
            return decodeURIComponent(cookiePair[1]);
        }
    }
    
    // Return null if not found
    return null;
}

export const AUTH_COOKIE_NAME = "Authorization"
export const UID_COOKIE_NAME = "UID"