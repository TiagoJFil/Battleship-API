import { ShipRules } from "./ship-rules";

export interface GameRules{
    boardSide: number,
    shotsPerTurn: number,
    layoutDefinitionTimeout: number,
    playTimeout: number,
    shipRules: ShipRules
}