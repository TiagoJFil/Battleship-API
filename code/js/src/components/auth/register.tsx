import * as React from 'react';
import { fetchRegister } from './../../api/api'
import { setAuthInfo } from './../../api/session';

export function Register(){

    const [username, setUsername] = React.useState<string>('');
    const [password, setPassword] = React.useState<string>('');
    const [error, setError] = React.useState<string | null>(null);


    const onRegisterClick = async () => {
        try {
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
            <div>
                <div>
                    <label>Username:</label>
                    <input type="text" onChange={e => setUsername(e.target.value)} />
                </div>
                <div>
                    <label> Password:</label>
                    <input type="password"  onChange={ e => setPassword(e.target.value)} />
                </div>
                <button onClick={onRegisterClick}>Register</button>
            </div>
            {error && <div>{error}</div>}
        </div>
    )
}

