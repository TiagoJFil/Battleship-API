import { Ship } from "./ship";


export interface GameRules{
    ships: Ship[],
    boardSide: number,
    layoutDefinitionTimeout: number
}