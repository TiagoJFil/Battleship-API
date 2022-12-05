import * as React from 'react'
import { fetchLogin } from '../../../api/api'
import { setAuthInfo } from '../../../api/session';
import { validateAuth } from '../../../AuthValidation';
import { AuthForm } from '../authForm';
import { ErrorToast } from '../../../toasts';
import { BottomNav } from '../../bottomNav';
import { Link } from 'react-router-dom'


export function Login() {

    const onLoginClick = async (username : string,password : string) => {
        try {
            validateAuth(username,password)

            const authInformation = await fetchLogin(username, password);
            console.log(authInformation)

            setAuthInfo(authInformation.properties);
        } catch (e) {
            console.log(e)
            ErrorToast(e.title).showToast();
        }
    }


    return (
        <div>
            <BottomNav/>
            <h1>Login</h1>
            <AuthForm confirmPrompt="Login" onSubmit={(username,password) => {onLoginClick(username,password)} } />   
            Dont have an account? <Link to="/register">Register</Link>
        </div>
    )

} 