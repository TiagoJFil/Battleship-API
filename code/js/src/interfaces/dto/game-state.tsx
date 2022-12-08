import { GameState } from "../../components/entities/game-state";

export interface GameStateInfoDTO {
    state: GameState,
    winnerID: number
}
