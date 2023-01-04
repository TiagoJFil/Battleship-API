import * as React from 'react'
import Box from '@mui/material/Box';
import Input from '@mui/material/Input';
import Button from '@mui/material/Button';
import { Link } from 'react-router-dom';

/**
 * Renders a form for logging in or registering.
 * @param prop properties of the component: 
 * ``{
 *      onSubmit: (username:string, password:string) => void
 *      confirmPrompt : string
 * }``
 */
export function AuthForm(prop : 
    {onSubmit :  (e : any ,username:string, password:string) => void,
         confirmPrompt? : string,
         sideText : string,
         sideLink : string,
         sideLinkText : string}) {
    const [username, setUsername] = React.useState('')
    const [password, setPassword] = React.useState('')
    const [passwordShown, setPasswordShown] = React.useState(false)

    const togglePasswordVisiblity = () => {
        setPasswordShown(passwordShown ? false : true);
    };
    return (
        <div className="cloud-container">
            <Box
            component="form"
            sx={{
            '& > :not(style)': { m: 1 },
            }}
            noValidate
            autoComplete="off"
            >
                <Input placeholder="Username" onChange={e => setUsername(e.target.value)} />
                <Input placeholder="Password" type={passwordShown ? "text" : "password"} onChange={e => setPassword(e.target.value)}  />
                <Button onClick={togglePasswordVisiblity}>{passwordShown ? "Hide" : "Show"}</Button>
                <Button onClick={ (e : any) => prop.onSubmit(e,username,password) }>{prop.confirmPrompt ?? "Confirm"}</Button>
            
            </Box>
            {prop.sideText} <Link to={prop.sideLink}>{prop.sideLinkText}</Link>
        </div>
      
    )
}