import { IShipRulesDTO } from "./ship-rules-dto";

export interface IGameRulesDTO{
    boardSide: number,
    shotsPerTurn: number,
    layoutDefinitionTimeout: number,
    playTimeout: number,
    shipRules: IShipRulesDTO
}