export interface IPosition{
    x: number,
    y: number
}

export class Position implements IPosition{
    x: number
    y: number
    constructor(x: number, y: number){
        this.x = x
        this.y = y
    }
}