import * as React from 'react';
import { validateAuth } from '../../validations/auth-validation';
import { fetchRegister } from '../../api/api'
import { AuthForm } from './auth-form';
import { useNavigate } from 'react-router-dom';
import { ErrorToast } from './../../core-ui/toasts';

export function Register(){
    const navigate = useNavigate()

    const onRegisterClick = async (username,password) => {
        try {
            validateAuth(username,password)
            await fetchRegister(username, password);
            navigate('/')
        } catch (e ) {
            console.log(e)
            ErrorToast(e.title).showToast();
        }
    }

    return (
        <div>
            <h1>Register</h1>
            <AuthForm confirmPrompt="Register" onSubmit={(username,password) => {onRegisterClick(username,password)} }/>
        </div>
    )
}

