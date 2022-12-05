export interface Statistics {
    ngames: number,
    ranking: Array<PlayerStatistics>
}

export interface PlayerStatistics {
    rank: number,
    playerID: number,
    totalGames: number,
    wins: number
}
