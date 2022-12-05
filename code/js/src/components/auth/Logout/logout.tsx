import * as React from 'react'
import { logout } from '../../../api/session'

export function Logout(){


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