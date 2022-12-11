import { SquareDTO, Row, Column } from "../../interfaces/dto/square"

export class Square{
    row: number
    column: number

    constructor(row: number, column: number){
        this.row = row
        this.column = column
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