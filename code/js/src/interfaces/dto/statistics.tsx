export interface StatisticsDTO {
    ngames: number,
    ranking: Array<PlayerStatisticsDTO>
}

export interface PlayerStatisticsDTO {
    rank: number,
    playerID: number,
    totalGames: number,
    wins: number
}
