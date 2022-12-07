import { ShipRulesDTO } from "./ship-rules";

export interface GameRulesDTO{
    boardSide: number,
    shotsPerTurn: number,
    layoutDefinitionTimeout: number,
    playTimeout: number,
    shipRules: ShipRulesDTO
}