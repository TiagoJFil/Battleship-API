import * as React from 'react';
import { getStatistics } from '../api/api';
import {StatisticsDTO, PlayerStatisticsDTO} from '../interfaces/dto/statistics';
import { SirenEntity } from '../interfaces/hypermedia/siren';

export function Statistics() {

    const [statistics, setStatistics] = React.useState<StatisticsDTO | null>(null);

    React.useEffect(() => {
        getStatistics().then( (res : SirenEntity<StatisticsDTO>) => setStatistics(res.properties));
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
                    {statistics.ranking.map((playerStatistics: PlayerStatisticsDTO) => (
                        <tr key={playerStatistics.playerID}>
                            <td>{playerStatistics.rank}</td>
                            <td>{playerStatistics.playerID}</td>
                            <td>{playerStatistics.totalGames}</td>
                            <td>{playerStatistics.wins}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}
