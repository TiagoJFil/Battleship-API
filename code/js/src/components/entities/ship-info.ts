import { ISquareDTO } from "../../interfaces/dto/square-dto"
import { Orientation } from "./orientation"

export class ShipInfo{
    initialSquare: ISquareDTO
    size: number
    orientation: Orientation

    constructor(initialSquare:ISquareDTO, size: number, orientation: Orientation){
        this.initialSquare = initialSquare
        this.size = size
        this.orientation = orientation
    }
}