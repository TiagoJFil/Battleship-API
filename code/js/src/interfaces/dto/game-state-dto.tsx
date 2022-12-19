import { GameState } from "../../components/entities/game-state";

export interface IGameStateInfoDTO {
    state: GameState,
    turnID: number,
    player1ID: number,
    player2ID: number
}
