import { GameState } from "../../components/entities/game-state";

export interface IGameStateInfoDTO {
    state: GameState,
    player1ID: number,
    player2ID: number,
    turnID: number,
}
