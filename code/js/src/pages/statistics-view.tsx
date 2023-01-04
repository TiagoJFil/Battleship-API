import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { authServices } from '../api/auth';
import {IStatisticsDTO, INamedPlayerStatisticsDTO} from '../interfaces/dto/statistics-dto';
import { getStatisticsWithEmbeddedPlayers } from '../utils/utils';
import { Typography } from "@mui/material";
import { CircularProgress } from '@mui/material';
import { StatisticsTable } from '../components/statistics-table';
import "../css/statistics.css"

export function Statistics() {
    const [statistics, setStatistics] = React.useState<IStatisticsDTO | null>(null);

    React.useEffect(() => {
        const getStatisticsInfo = async () => {
            const stats = await getStatisticsWithEmbeddedPlayers();
            setStatistics(stats);
        }
        getStatisticsInfo();
    }, []);

    if (statistics === null) {
        return  <div className='screen-centered'> 
                    <CircularProgress size='6rem' />
                </div>
    }

    const tableHeaders = ['Rank', 'Player Name', 'Total Games', 'Wins'];

    return (
        <div className='page'>
            <Typography className='app-title' align='center' variant="h2">Statistics</Typography>
            <div className='statistics-center'>

                <div className=' statistics-container'>
                    <Typography align='center' variant="body1">Total Games: {statistics.ngames}</Typography>
            
            
                    <StatisticsTable headers={tableHeaders} data={statistics.ranking as INamedPlayerStatisticsDTO[]} />
            
                </div>
            </div>
            





        </div>
    );
}
