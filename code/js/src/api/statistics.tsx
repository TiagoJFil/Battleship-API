
export interface Statistics {
    nGames: number,
    ranking: Array<PlayerStatistics>
}

export interface PlayerStatistics {
    rank: number,
    playerID: number,
    totalGames: number,
    wins: number
}


export async function getStatistics(): Promise<Statistics> {
    const response = await fetch('/api/statistics')

    const siren = await response.json()
    return siren.properties
}