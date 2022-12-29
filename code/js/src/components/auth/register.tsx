import * as React from 'react';
import { validateAuth } from '../../validations/auth-validation';
import { fetchRegister } from '../../api/api'
import { AuthForm } from './auth-form';
import { useNavigate } from 'react-router-dom';
import { ErrorToast } from './../../core-ui/toasts';
import { executeWhileDisabled } from '../../utils/ButtonWrappers';

export function Register(){
    const navigate = useNavigate()

    const onRegisterClick = (event : any,username : string,password : string) => {
        
        const button: HTMLButtonElement = event.target;

        executeWhileDisabled(button, async () => {
            // Current millis
            const start = Date.now();
            try {
                validateAuth(username,password)
                await fetchRegister(username, password);
                navigate('/')
            } catch (e ) {
                const end = Date.now();
                ErrorToast(e.title);
                console.log("Time taken: " + (end - start) + "ms");
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

