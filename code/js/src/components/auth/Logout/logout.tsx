import * as React from 'react'
import { useNavigate } from 'react-router-dom'
import { authServices } from '../../../api/auth'


export function Logout(){
    const navigate = useNavigate()

    const onlogout = () => {
        authServices.logout()
        navigate('/')
    }

    return(

        <div>
        <h1>Logout:</h1>
        <input type="button" value="Logout" onClick={onlogout}/>
        </div>
    )
}