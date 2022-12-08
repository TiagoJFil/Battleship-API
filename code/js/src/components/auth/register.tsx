import * as React from 'react';
import { validateAuth } from '../../validations/auth-validation';
import { fetchRegister } from '../../api/api'
import { setAuthInfo } from '../../api/session';
import { AuthForm } from './auth-form';

export function Register(){
    const [error, setError] = React.useState<string | null>(null);


    const onRegisterClick = async (username,password) => {
        try {
            validateAuth(username,password)
            const authInformation = await fetchRegister(username, password);
            console.log(authInformation)
            setAuthInfo(authInformation.properties);

        } catch (e ) {
            console.log(e)
            setError(e.title);
        }
    }

    return (
        <div>
            <h1>Register</h1>
            <AuthForm confirmPrompt="Register" onSubmit={(username,password) => {onRegisterClick(username,password)} }/>
            {error && <div>{error}</div>}
        </div>
    )
}

