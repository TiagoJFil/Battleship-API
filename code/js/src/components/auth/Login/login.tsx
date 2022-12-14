import * as React from 'react'
import { fetchLogin } from '../../../api/api'
import { validateAuth } from '../../../validations/auth-validation';
import { AuthForm } from '../auth-form';
import { ErrorToast } from '../../../core-ui/toasts';
import { BottomNav } from '../../bottom-nav';
import { Link, useNavigate } from 'react-router-dom'


export function Login() {
    const navigate = useNavigate()

    const onLoginClick = async (username : string,password : string) => {
        try {
            validateAuth(username,password)
            await fetchLogin(username, password);
            navigate('/')
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