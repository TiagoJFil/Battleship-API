
import { Ship } from "../entities/ship"
import { Square } from "../entities/square"
import { SquareType } from "../entities/square-type"
import { Board } from "../entities/board"


const BreakError = {}

/**
 * 
 */
 function getDiagonals(square: Square): Square[]{
    const topLeft = new Square(square.row - 1, square.column - 1)
    const topRight = new Square(square.row - 1, square.column + 1)
    const bottomLeft = new Square(square.row + 1, square.column - 1)
    const bottomRight = new Square(square.row + 1, square.column + 1)
  
    return [topLeft, topRight, bottomLeft, bottomRight]
  }
  
  /**
   * 
   * @param square 
   * @returns 
   */
  function getAxisNeighbours(square: Square): Square[]{
    const top = new Square((square.row - 1), square.column)
    const bottom = new Square((square.row + 1), square.column)
    const left = new Square(square.row, (square.column - 1))
    const right = new Square(square.row, (square.column + 1))
  
    return [top, bottom, left, right]
  }
  
  /**
   * 
   * @param square 
   * @returns 
   */
  function getSurrounding(square: Square): Square[]{
    return getAxisNeighbours(square).concat(getDiagonals(square))
  }
  
  /**
   * 
   */
  function isValid(square: Square, boardSide: number): boolean{
    return square.column >= 0 && square.column < boardSide && square.row >= 0 && square.row < boardSide
  }
  
  /**
   * 
   */
  function filterInBounds(boardSide: number, squares: Square[]): Square[]{
    return squares.filter((square) => isValid(square, boardSide))
  }
  
  /**
   * 
   */
  export function hasAdjacentShips(
    ship: Ship,
    initialSquare: Square,
    board: Board
  ): boolean{
    const boardRepresentation = board.asMap()
    const seen = []
    const unchecked = ship.getSquares(initialSquare).slice()
  
    while(unchecked.length > 0){
      const square = unchecked.pop()
      const neighbours = getSurrounding(square) 
      const neighboursInBounds = filterInBounds(board.side, neighbours)

      try{
        neighboursInBounds.forEach((neighbour) => {
          if(!seen.includes(neighbour)){
              const type = boardRepresentation.get(neighbour.toID()) ?? SquareType.WATER
              
              if(type === SquareType.SHIP_PART){
                throw BreakError
              }
            seen.push(neighbour)
          }
        })
      }catch(err){
        if(err !== BreakError) throw err
        return true
      }
    } 
    return false
  }

/**
 * Checks if a ship to be placed is within the board
 * 
 * @param ship the ship to be placed
 * @param boardSide length of a row or column of the board
 * @returns true if the ship is within the board, false otherwise
 */
 export function isWithinBounds(ship: Ship, initialSquare: Square, board: Board): boolean{
    const shipSquares = ship.getSquares(initialSquare)
    return shipSquares.every((square) => board.isWithinBounds(square))
}

/**
 * Checks if a ship to be placed overlaps with another ship
 * 
 * @param ship the ship to be placed
 * @param board the board where the ship is to be placed
 * @param boardSide length of a row or column of the board
 * @returns true if the ship does not overlap with another ship, false otherwise
 */
 export function isPlaceFree(
    ship: Ship, 
    initialSquare: Square,
    board: Board, 
  ): boolean{
    const shipSquares = ship.getSquares(initialSquare)
    return board.shipParts.every ((square) => !shipSquares.includes(square))
}
