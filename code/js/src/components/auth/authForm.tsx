import * as React from 'react'

/**
 * Renders a form for logging in or registering.
 * @param prop properties of the component: 
 * ``{
 *      onSubmit: (username:string, password:string) => void
 *      confirmPrompt : string
 * }``
 */
export function AuthForm(prop) {
    const [username, setUsername] = React.useState('')
    const [password, setPassword] = React.useState('')
    const [passwordShown, setPasswordShown] = React.useState(false)

    const togglePasswordVisiblity = () => {
        setPasswordShown(passwordShown ? false : true);
    };

    return (
        <div>
            <div>
                <label>Username</label>
                <input type="text" onChange={e => setUsername(e.target.value)} />
            </div>
            <div>
                <label>Password</label>
                <input type={passwordShown ? "text" : "password"} onChange={e => setPassword(e.target.value)} />
                <button onClick={togglePasswordVisiblity}>{passwordShown ? "Hide" : "Show"}</button>
            </div>
            <button onClick={() => prop.onSubmit(username,password)}>{prop.confirmPrompt ?? "Confirm"}</button>
        </div>
    )

}