import { SquareType } from "../game/game"
import { Orientation } from "../utils/orientation"
import { Ship } from "../utils/ship"
import { Position } from "../utils/position"

export const typeToClass = {
    ["#"]: "water",
    ["B"]: "ship-part",
    ["O"]: "shot",
    ["X"]: "hit", 
    ["F"]: "forbidden"
}

const BreakError = {}

/**
 * Returns the index of the square in the board given its position
 * 
 * @param position position in the board: {x: 0, y: 0}
 * @param boardSide length of a row or column of the board
 * @returns the index of the square in the board array
 */
export function positionToIndex(position: Position, boardSide: number): number{
  if(position === undefined) return undefined
  const { x, y } = position
  return y * boardSide + x
}

/**
 * Returns the position of the square in the board given its index
 * 
 * @param index index of the square in the board array 
 * @param boardSide length of a row or column of the board
 * @returns the position of the square in the board
 */
export function indexToPosition(index: number, boardSide: number): Position{
  const x = index % boardSide
  const y = Math.floor(index / boardSide)

  return new Position(x, y)
}

/**
 * Gets all the positions of the squares that a ship occupies
 * 
 * @param ship a placeable that represents a ship
 * @param boardSide length of a row or column of the board
 * @returns an array with the positions that the ship occupies
 */
export function shipSquaresBoardPosition(ship: Ship, boardSide: number): number[]{
  let index = positionToIndex(ship.position, boardSide);
  let indices = [];

  for (let i = 0; i < ship.size; i++) {
    indices.push(index);
    index = ship.orientation === Orientation.vertical ? index + boardSide : index + 1;
  }

  return indices;
}

/**
 * Places a ship in the board
 * 
 * @param previousBoard  the board before placing the ship
 * @param boardSide  length of a row or column of the board
 * @param currentlyPlacingship  the ship to be placed
 * @returns the board after placing the ship
 */
export function putShipInBoard(
    previousBoard: SquareType[],
    boardSide: number,
    currentlyPlacingship: Ship,
    type: SquareType
): SquareType[] {
    const newBoard = previousBoard.slice()
    const shipIndices = shipSquaresBoardPosition(currentlyPlacingship, boardSide)
    
    shipIndices.forEach(function(index){
        newBoard[index] = type
    })

    return newBoard
}

/**
 * Checks if a ship to be placed is within the board
 * 
 * @param ship the ship to be placed
 * @param boardSide length of a row or column of the board
 * @returns true if the ship is within the board, false otherwise
 */
export function isWithinBounds(ship: Ship, boardSide: number): boolean{
  return (
    (ship.orientation === Orientation.vertical && ship.position.y + ship.size <= boardSide) ||
    (ship.orientation === Orientation.horizontal && ship.position.x + ship.size <= boardSide)
  )
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
  board: SquareType[], 
  boardSide: number
): boolean{
  const shipIndices = shipSquaresBoardPosition(ship, boardSide)
  return shipIndices.every((idx) => board[idx] === SquareType.water)
}

/**
 * 
 */
function getDiagonals(position: Position): Position[]{
  const topLeft = new Position(position.x - 1, position.y - 1)
  const topRight = new Position(position.x + 1, position.y - 1)
  const bottomLeft = new Position(position.x - 1, position.y + 1)
  const bottomRight = new Position(position.x + 1, position.y + 1)

  return [topLeft, topRight, bottomLeft, bottomRight]
}

/**
 * 
 * @param square 
 * @returns 
 */
function getAxisNeighbours(position: Position): Position[]{
  const top = new Position(position.x, position.y - 1)
  const bottom = new Position(position.x, position.y + 1)
  const left = new Position(position.x - 1, position.y)
  const right = new Position(position.x + 1, position.y)

  return [top, bottom, left, right]
}

/**
 * 
 * @param square 
 * @returns 
 */
function getSurrounding(position: Position): Position[]{
  return getAxisNeighbours(position).concat(getDiagonals(position))
}

/**
 * 
 */
function isValid(position: Position, boardSide: number): boolean{
  return position.x >= 0 && position.x < boardSide && position.y >= 0 && position.y < boardSide
}

/**
 * 
 */
function filterInBounds(boardSide: number, positions: Position[]): Position[]{
  const boardMaxIndex = boardSide * boardSide
  return positions.filter((position) => {
    const index = positionToIndex(position, boardSide)
     return isValid(position, boardSide) && 
            index < boardMaxIndex && 
            index >= 0    
   })
}

/**
 * 
 */
export function checkForAdjacentShips(
  ship: Ship,
  boardSide: number,
  board: SquareType[]
): boolean{
  const shipIndices = shipSquaresBoardPosition(ship, boardSide)
  const seen = []
  const shipSquares = shipIndices.map((idx) => {
    const position = indexToPosition(idx , boardSide)
    seen.push(position)
    return position
  })
  const unchecked = shipSquares.slice()

  while(unchecked.length > 0){
    const square = unchecked.pop()
    const neighbours = getSurrounding(square) 
    const neighboursInBounds = filterInBounds(boardSide, neighbours)

    try{
      neighboursInBounds.forEach((neighbour) => {
        if(!seen.includes(neighbour)){
          const neighbourIndex = positionToIndex(neighbour, boardSide)
          if(board[neighbourIndex] === SquareType.ship_part){
            throw BreakError
          }
          seen.push(neighbour)
        }
      })
    }catch(err){
      if(err !== BreakError) throw err
      return false
    }
  } 
  return true
}


/**
 * Checks if a ship can be placed in the board
 * 
 * @param ship 
 * @param board 
 * @param boardSide 
 * @returns 
 */
export function canBePlaced(
  ship: Ship,
  board: SquareType[], 
  boardSide: number
): boolean{
  return isWithinBounds(ship, boardSide) && isPlaceFree(ship, board, boardSide) && checkForAdjacentShips(ship, boardSide, board)
}

/**
 * Calculates by how many squares a ship is out of bounds
 * 
 * @param ship the ship to be placed
 * @param boardSide length of a row or column of the board
 * @returns the number of squares the ship is out of bounds
 */
export function calculateOverhang(ship: Ship, boardSide:number): number{
  return Math.max(
    ship.orientation === 'Vertical'
      ? ship.position.y + ship.size - boardSide
      : ship.position.x + ship.size - boardSide,
    0
  );
}
