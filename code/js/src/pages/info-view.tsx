import * as React from 'react'
import { getSystemInfo } from '../api/api';
import { ISystemInfoDTO } from '../interfaces/dto/system-info-dto';
import { SirenEntity } from '../interfaces/hypermedia/siren';
import { Typography } from "@mui/material";
import { CircularProgress } from '@mui/material';

export function Info(){
    const [systemInfo , setSytemInfo] = React.useState<ISystemInfoDTO >(null)


    React.useEffect(() => {
        getSystemInfo()
        .then( (res : SirenEntity<ISystemInfoDTO>) => setSytemInfo(res.properties));
    }, []);

    if (systemInfo === null) {
        return (
        <div className='screen-centered'> 
            <CircularProgress size='6rem' />
        </div>
        )
    }

    return (
        <div className='page'>
            <Typography className='app-title' align='center' variant="h2">System Info</Typography>
            <div className='center-container'>
                <div className='cloud-container'>
                    <Typography variant="body1">System version: {systemInfo.version}</Typography>
                    <Typography variant='body1'> Authors: </Typography>
                    <ul>
                        {systemInfo.authors.map((author) => (
                            <li key={author.name}>
                                <a href={author.github}>{author.iselID} {author.name}</a>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    )
}