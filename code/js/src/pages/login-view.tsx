import * as React from 'react'
import { Link, useNavigate } from 'react-router-dom';
import { fetchLogin } from '../api/api';
import { AuthForm } from '../components/auth/auth-form';
import { ErrorToast } from '../core-ui/toasts';
import { executeWhileDisabled } from '../utils/ButtonWrappers';
import { validateAuth } from '../validations/auth-validation';
import {  Typography } from "@mui/material";
import "../css/login.css"

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
        <div>
            <Typography align='center' variant="h2">Login</Typography>
            <AuthForm confirmPrompt="Login" onSubmit={(event,username,password) => {onLoginClick(event,username,password)} } />   
        </div>
    )

} 