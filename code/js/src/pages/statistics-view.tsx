import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { authServices } from '../api/auth';
import {IStatisticsDTO, INamedPlayerStatisticsDTO} from '../interfaces/dto/statistics-dto';
import { getStatisticsWithEmbeddedPlayers } from '../utils/utils';



export function Statistics() {
    const navigate = useNavigate();

    const [statistics, setStatistics] = React.useState<IStatisticsDTO | null>(null);

    React.useEffect(() => {
        

        const getStatisticsInfo = async () => {
            const stats = await getStatisticsWithEmbeddedPlayers();
            setStatistics(stats);
        }
        getStatisticsInfo();
    }, []);

    if (statistics === null) {
        return <div>Loading...</div>;
    }

    return (
        <div>
            <h1>Statistics</h1>
            <p>Number of games: {statistics.ngames}</p>
            <table>
                <thead>
                    <tr>
                        <th>Rank</th>
                        <th>Player ID</th>
                        <th>Total Games</th>
                        <th>Wins</th>
                    </tr>
                </thead>
                <tbody>
                    {statistics.ranking.map((playerStatistics: INamedPlayerStatisticsDTO) => (
                        <tr key={playerStatistics.rank}>
                            <td>{playerStatistics.rank}</td>
                            <td>{playerStatistics.playerName}</td>
                            <td>{playerStatistics.totalGames}</td>
                            <td>{playerStatistics.wins}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
