import { Ship } from "./ship"
import { Square } from "./square"
import { SquareType } from "./square-type"
import { hasAdjacentShips, isPlaceFree, isWithinBounds } from "./utils"


export class Board{
    side: number
    shipParts: Square[]
    shots: Square[]
    hits: Square[]
    invalidSquares: Square[]

    constructor(
        side: number, 
        shipParts: Square[], 
        shots: Square[], 
        hits: Square[], 
        invalidSquares: Square[] = [],
    ){
        this.side = side
        this.shipParts = shipParts
        this.shots = shots
        this.hits = hits
        this.invalidSquares = invalidSquares
    }

    /**
     * Converts a board into a map with the square's type
     * @returns a map with all the square's type
     */
    asMap(): Map<string, SquareType>{
        const map = new Map<string, SquareType>()
        
        this.toSquareType(map, this.shipParts, SquareType.SHIP_PART)
        this.toSquareType(map, this.shots, SquareType.SHOT)
        this.toSquareType(map, this.hits, SquareType.HIT)
        this.toSquareType(map, this.invalidSquares, SquareType.INVALID)
        return map
    }   

    /**
     * Places an invalid ship on the board, it only shows the squares that are in bounds
     * @param ship the ship to place on the board
     * @param initialSquare the square that was clicked
     * @returns a new board with the ship placed
     */
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

    /**
     * Place a ship on the board
     * @param ship the ship to place on the board
     * @param initialSquare the square that was clicked
     * @returns a new board with the ship placed
     */
    place(ship: Ship, initialSquare: Square): Board{
        return new Board(
            this.side,
            this.shipParts.concat(ship.getSquares(initialSquare)),
            this.shots,
            this.hits
        )
    }

    /**
     * Checks if a square is within the board's bounds
     * @param square the square to check if it's within board's bounds
     * @returns true if the square is within the board's bounds
     */
    isWithinBounds(square: Square): boolean{
        return (
            ((square.row < this.side) && (square.row >= 0)) &&
            ((square.column < this.side) && (square.column >= 0))
        )
    }

    /**
     * Checks if a ship can be placed in the board
     * 
     * @param ship ship to be placed
     * @param initialSquare square that was clicked
     * @returns true if the ship can be placed
     */
    canPlace(
        ship: Ship,
        initialSquare: Square 
    ): boolean{
        return isWithinBounds(ship, initialSquare, this) && isPlaceFree(ship, initialSquare, this) && !hasAdjacentShips(ship, initialSquare, this)
    }

    /**
     * Maps all the squares to its corresponding type
     * @param map map to add the square types to
     * @param array array with the squares to map to a type
     * @param type  square type
     */
    private toSquareType(map: Map<string, SquareType>, array: Square[], type: SquareType){
        return array.forEach((square) => map.set(square.toID(), type))
    }
}

/**
 * Creates an empty board
 * 
 * @param side the side of the board
 * @returns an empty board
 */
export function emptyBoard(side: number): Board{
    return new Board(side, [], [], [], [])
}

