import * as React from 'react'
import { useNavigate } from 'react-router-dom'
import { authServices } from '../api/auth'
import {  Typography } from "@mui/material";
import Button from '@mui/material/Button';


export function Logout(){
    const navigate = useNavigate()

    const onlogout = () => {
        authServices.logout()
        navigate('/')
    }
    const styles = {
        width : '100%',
        height : '100%',
    }
    return(
        <div className='page'>
            <Typography className='app-title' align='center' variant="h2">Logout</Typography>
            <div className='screen-centered'> 
                <div className='cloud-container'>
                    <Button style={styles} onClick={onlogout}>Logout </Button>
                </div>
            </div>
        </div>
    )
}