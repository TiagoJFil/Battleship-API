import * as React from 'react';
import { Typography } from "@mui/material"
import { List } from '@mui/material';

export default function NotFound(){
    return(
        <div className='page'>
        
            <div className='center-container'>
                <List >
                    <Typography className='app-title' align='center' variant="h1">404</Typography>
                    <Typography className='app-title' align='center' variant="h2">Page not found</Typography>
                </List>
                
            </div>
        </div>
    )
}