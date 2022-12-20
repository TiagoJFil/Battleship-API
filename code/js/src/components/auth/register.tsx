import * as React from 'react';
import { validateAuth } from '../../validations/auth-validation';
import { fetchRegister } from '../../api/api'
import { AuthForm } from './auth-form';
import { useNavigate } from 'react-router-dom';
import { ErrorToast } from './../../core-ui/toasts';
import { DisableButtonWhileOnClickWrapper } from '../../utils/ButtonWrappers';

export function Register(){
    const navigate = useNavigate()

    const onRegisterClick = (event : any,username : string,password : string) => {
        DisableButtonWhileOnClickWrapper(event, async () => {
            try {
                validateAuth(username,password)
                await fetchRegister(username, password);
                navigate('/')
            } catch (e ) {
                console.log("asdasd")
                console.log(e)
                ErrorToast(e.title);
            }
        })
    }

    return (
        <div>
            <h1>Register</h1>
            <AuthForm confirmPrompt="Register" onSubmit={(event,username,password) => {onRegisterClick(event,username,password)} }/>
        </div>
    )
}

