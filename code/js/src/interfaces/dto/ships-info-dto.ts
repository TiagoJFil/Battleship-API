import { Orientation } from "../../components/entities/orientation";
import { ISquareDTO } from "./square-dto";

export interface IShipInfoDTO {
    initialSquare: ISquareDTO,
    orientation: Orientation,
    size: number
}