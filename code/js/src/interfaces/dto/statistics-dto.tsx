export interface IStatisticsDTO {
    ngames: number,
    ranking: Array<IPlayerStatisticsDTO>
}

export interface IPlayerStatisticsDTO {
    rank: number,
    playerID: number,
    totalGames: number,
    wins: number
}
