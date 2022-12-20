import * as React from 'react'
import { fetchLogin } from '../../../api/api'
import { validateAuth } from '../../../validations/auth-validation';
import { AuthForm } from '../auth-form';
import { ErrorToast } from '../../../core-ui/toasts';
import { BottomNav } from '../../bottom-nav';
import { Link, useNavigate } from 'react-router-dom'
import { DisableButtonWhileOnClickWrapper } from '../../../utils/ButtonWrappers';


export function Login() {
    const navigate = useNavigate()

    const onLoginClick = (event : any,username : string,password : string) => {

        DisableButtonWhileOnClickWrapper(event, async () => {
            try {
                validateAuth(username,password)
                await fetchLogin(username, password);
                navigate('/')
            } catch (e) {
                ErrorToast(e.title);
            }
        })
    }

    return (
        <div>
            <BottomNav/>
            <h1>Login</h1>
            <AuthForm confirmPrompt="Login" onSubmit={(event,username,password) => {onLoginClick(event,username,password)} } />   
            Dont have an account? <Link to="/register">Register</Link>
        </div>
    )

} 