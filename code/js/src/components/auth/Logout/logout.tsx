import * as React from 'react'
import { logout } from '../../../api/session'

export function Logout(){

    const [isLogged, setIsLogged] = React.useState(false)
    const [isLoggedOut, setIsLoggedOut] = React.useState(false)

    const onlogout = () => {
        logout()
    }


    return(

        <div>
        <h1>Logout:</h1>
        <input type="button" value="Logout" onClick={onlogout}/>
        </div>
    )
}