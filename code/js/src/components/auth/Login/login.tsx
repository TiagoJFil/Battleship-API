import * as React from 'react'
import { fetchLogin } from '../../../api/api'
import { setAuthInfo } from '../../../api/session';

export function Login() {

    const [username, setUsername] = React.useState<string>('');
    const [password, setPassword] = React.useState<string>('');
    const [error, setError] = React.useState<string | null>(null);

    return StatelessLogin(
        async () => {
            try {
                const authInformation = await fetchLogin(username, password);
                console.log(authInformation)
    
                setAuthInfo(authInformation.properties);
            } catch (e ) {
                console.log(e)
                setError(e.title);
            }
        },
        setUsername,
        setPassword,
        error
    )

}


function StatelessLogin(
    onLoginClick: () => void,
    setUsername = (_username: string) => {},
    setPassword = (_password: string) => {},
    error?: string | null
    ) {

    return (
        <div>
            <h1>Login</h1>
            <div>
                <label>Username</label>
                <input type="text" onChange={e => setUsername(e.target.value)} />
            </div>
            <div>
                <label>Password</label>
                <input type="password" onChange={e => setPassword(e.target.value)} />
            </div>
            <button onClick={onLoginClick}>Login</button>
            {error && <div>{error}</div>}
        </div>
    )
}
