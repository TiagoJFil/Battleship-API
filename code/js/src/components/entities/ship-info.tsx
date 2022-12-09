import { SquareDTO } from "../../interfaces/dto/square"
import { Orientation } from "./orientation"

export class ShipInfo{
    initialSquare: SquareDTO
    size: number
    orientation: Orientation

    constructor(initialSquare:SquareDTO, size: number, orientation: Orientation){
        this.initialSquare = initialSquare
        this.size = size
        this.orientation = orientation
    }
}