import { Orientation } from "./orientation"
import { Square } from "./square"

export class Ship{
    id: number
    size: number
    orientation: Orientation

    constructor(id:number, size: number, orientation: Orientation){
        this.id = id
        this.size = size
        this.orientation = orientation
    }

    getSquares(initialSquare: Square): Square[]{
        const squares: Square[] = []
        for(let i = 0; i < this.size; i++){
            if(this.orientation === Orientation.horizontal){
                squares.push(new Square(initialSquare.row, initialSquare.column + i))
            } else {
                squares.push(new Square(initialSquare.row + i, initialSquare.column))
            }
        }
        return squares
    }

    rotate(): Ship{
        return new Ship(
            this.id, 
            this.size, 
            this.orientation === Orientation.horizontal ? Orientation.vertical : Orientation.horizontal
        )
    }
}
