import { Orientation } from "./orientation"
import { Position } from "./position"

export class Ship{
    size: number
    orientation: Orientation
    position: Position = null
    placed: Boolean = false

    constructor(size: number, orientation: Orientation, position: Position, placed: Boolean){
        this.size = size
        this.orientation = orientation
        this.position = position
        this.placed = placed
    }
}