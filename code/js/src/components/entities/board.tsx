import { Ship } from "./ship"
import { Square } from "./square"
import { SquareType } from "./square-type"

import { hasAdjacentShips, isPlaceFree, isWithinBounds } from "../board/utils"
import { SquareDTO } from "../../interfaces/dto/square"

export class Board{
    side: number
    shipParts: Square[]
    shots: Square[]
    hits: Square[]
    invalidSquares: Square[]

    constructor(side: number, shipParts: Square[], shots: Square[], hits: Square[], invalidSquares: Square[] = []){
        this.side = side
        this.shipParts = shipParts
        this.shots = shots
        this.hits = hits
        this.invalidSquares = invalidSquares
    }

    asMap(): Map<string, SquareType>{
        const map = new Map<string, SquareType>()
        
        this.shipParts.forEach((square) => map.set(square.toID(), SquareType.SHIP_PART))
        this.shots.forEach((square) => map.set(square.toID(), SquareType.SHOT))
        this.hits.forEach((square) => map.set(square.toID(), SquareType.HIT))
        this.invalidSquares.forEach((square) => map.set(square.toID(), SquareType.INVALID))
        return map
    }   

    placeInvalid(ship: Ship, initialSquare: Square): Board{
        const shipSquares = ship.getSquares(initialSquare)
        const invalidSquares = shipSquares.filter((square) => this.isWithinBounds(square))
        return new Board(
            this.side,
            this.shipParts,
            this.shots,
            this.hits,
            this.invalidSquares.concat(invalidSquares)
        )
    }

    place(ship: Ship, initialSquare: Square): Board{
        return new Board(
            this.side,
            this.shipParts.concat(ship.getSquares(initialSquare)),
            this.shots,
            this.hits
        )
    }

    isWithinBounds(square: Square): boolean{
        return (
            ((square.row < this.side) && (square.row >= 0)) &&
            ((square.column < this.side) && (square.column >= 0))
        )
    }

    /**
     * Checks if a ship can be placed in the board
     * 
     * @param ship 
     * @param board 
     * @param boardSide 
     * @returns 
     */
    canPlace(
        ship: Ship,
        initialSquare: Square 
    ): boolean{
        return isWithinBounds(ship, initialSquare, this) && isPlaceFree(ship, initialSquare, this) && !hasAdjacentShips(ship, initialSquare, this)
    }
}

export function emptyBoard(side: number): Board{
    return new Board(side, [], [], [], [])
}

export class BoardDTO{
    side: number
    shipParts: SquareDTO[]
    shots: SquareDTO[]
    hits: SquareDTO[]

    constructor(side: number, shipParts: SquareDTO[], shots: SquareDTO[], hits: SquareDTO[]){
        this.side = side
        this.shipParts = shipParts
        this.shots = shots
        this.hits = hits
    }
}