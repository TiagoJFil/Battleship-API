import { GameState } from "../../components/entities/game-state";

export interface IGameStateInfoDTO {
    state: GameState,
    winnerID: number
}
