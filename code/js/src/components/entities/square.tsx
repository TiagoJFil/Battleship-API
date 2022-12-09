import { SquareDTO, Row, Column } from "../../interfaces/dto/square"

export class Square{
    row: number
    column: number

    constructor(x: number, y: number){
        this.row = x
        this.column = y
    }

    toID(): string{
        return `${this.row}-${this.column}`
    }

    toDTO(): SquareDTO{
        return {
            row: new Row(this.row),
            column: new Column(this.column)
        }
    }
}