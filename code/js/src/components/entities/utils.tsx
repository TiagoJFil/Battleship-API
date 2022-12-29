
import { Ship } from "./ship"
import { Square } from "./square"
import { SquareType } from "./square-type"
import { Board } from "./board"

// Used to break out of a loop like a forEach
const BreakError = {}

/**
 * Gets the diagonals neighbours of a square
 * 
 * @param square the square to get the neighbours from
 * @returns an array with the neighbours
 */
 export function getDiagonals(square: Square): Square[]{
    const topLeft = new Square(square.row - 1, square.column - 1)
    const topRight = new Square(square.row - 1, square.column + 1)
    const bottomLeft = new Square(square.row + 1, square.column - 1)
    const bottomRight = new Square(square.row + 1, square.column + 1)
  
    return [topLeft, topRight, bottomLeft, bottomRight]
  }
  
  /**
   * Gets the vertical and horizontal neighbours of a square 
   * 
   * @param square the square to get the neighbours from
   * @returns an array with the neighbours
   */
  export function getAxisNeighbours(square: Square): Square[]{
    const top = new Square((square.row - 1), square.column)
    const bottom = new Square((square.row + 1), square.column)
    const left = new Square(square.row, (square.column - 1))
    const right = new Square(square.row, (square.column + 1))
  
    return [top, bottom, left, right]
  }
  
  /**
   * Gets the surrounding neighbour squares of a square
   * 
   * @param square the square to get the neighbours from
   * @returns an array with the neighbours
   */
  function getSurrounding(square: Square): Square[]{
    return getAxisNeighbours(square).concat(getDiagonals(square))
  }
  
  /**
   * Checks if a square is valid
   * 
   * @param square the square to check
   * @param boardSide the side of the board
   * @returns true if the square is valid
   */
  function isValid(square: Square, boardSide: number): boolean{
    return square.column >= 0 && square.column < boardSide && square.row >= 0 && square.row < boardSide
  }
  
  /**
   * Filters the squares that are within the board's bounds
   * 
   * @param boardSide the side of the board
   * @param squares the squares to filter
   */
  function filterInBounds(boardSide: number, squares: Square[]): Square[]{
    return squares.filter((square) => isValid(square, boardSide))
  }
  
  /**
   * Checks if a ship has adjacent ships
   * 
   * @param ship the ship to check
   * @param initialSquare the square that was clicked to place the ship
   * @param board the board where the ship is placed
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
   * Gets the squares of a ship
   * 
   * @param initialSquare the square that was clicked to place the ship
   * @param board the board where the ship is placed
   */
  export function getShipSquares(
    initialSquare: Square,
    board: Board
  ): Square[]{
    const boardRepresentation = board.asMap()
    const seen = [initialSquare]
    const frontier = []
    frontier.push(initialSquare)

    while(frontier.length > 0){
      const square = frontier.pop()
      const neighbours = getAxisNeighbours(square)
      const notSeenParts = neighbours.filter((neighbour) => {
         const squareType = boardRepresentation.get(neighbour.toID()) ?? SquareType.WATER
         return (squareType === SquareType.SHIP_PART || squareType === SquareType.HIT) &&
                !seen.includes(neighbour)
      })
      notSeenParts.forEach((part) => {
        seen.push(part)
        frontier.push(part)
      })
    }

    return seen
  }

 /**
 * Checks if a ship to be placed is within the board
 * 
 * @param ship the ship to be placed
 * @param initialSquare the square that was clicked to place the ship
 * @param board the board where the ship is to be placed
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
 * @param initialSquare the square that was clicked to place the ship
 * @param board the board where the ship is to be placed
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
