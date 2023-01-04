import * as React from 'react'
import { useNavigate } from 'react-router-dom';
import { fetchLogin } from '../api/api';
import { AuthForm } from '../components/auth/auth-form';
import { ErrorToast } from '../core-ui/toasts';
import { executeWhileDisabled } from '../utils/buttonWrappers';
import { validateAuth } from '../validations/auth-validation';
import { Typography } from "@mui/material";
export function Login() {
    
    const navigate = useNavigate()

    const onLoginClick = (event : any, username : string, password : string) => {
        executeWhileDisabled(event.target, async () => {
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
        <div className='page'>
            <Typography className='app-title' align='center' variant="h2">Login</Typography>
            <div className='center-container'>
                <AuthForm confirmPrompt="Login" onSubmit={(event,username,password) => {onLoginClick(event,username,password)}} sideText="Dont have an account?" sideLink="/register" sideLinkText="Register" />       
            </div>
        </div>
    )

} 