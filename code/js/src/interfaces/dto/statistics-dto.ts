export interface IStatisticsDTO {
    ngames: number,
    ranking: Array<PlayerStats>
}

export interface IPlayerStatisticsDTO extends PlayerStats {
    rank: number,
    playerID: number,
    totalGames: number,
    wins: number
}
export interface INamedPlayerStatisticsDTO extends PlayerStats{
    rank : number,
    playerName: string,
    totalGames: number,
    wins: number
}
interface PlayerStats{}