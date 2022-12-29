import * as React from 'react'
import { getSystemInfo } from '../api/api';
import { ISystemInfoDTO } from '../interfaces/dto/system-info-dto';
import { SirenEntity } from '../interfaces/hypermedia/siren';


export function Info(){
    const [systemInfo , setSytemInfo] = React.useState<ISystemInfoDTO >(null)


    React.useEffect(() => {
        getSystemInfo()
        .then( (res : SirenEntity<ISystemInfoDTO>) => setSytemInfo(res.properties));
    }, []);

    if (systemInfo === null) {
        return (
        <div>
            <h1>System Info</h1>
            <p>Loading...</p>
        </div>
        )
    }

    return (
        <div>
            <h1>System Info</h1>
            <p>System version: {systemInfo.version}</p>
            <p>Authors:</p>
            <ul>
                {systemInfo.authors.map((author) => (
                    <li key={author.name}>
                        <a href={author.github}>{author.name}</a>
                    </li>
                ))}
            </ul>
        </div>
    )
}