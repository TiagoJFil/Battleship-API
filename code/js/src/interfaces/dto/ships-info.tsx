import { Orientation } from "../../components/entities/orientation";
import { SquareDTO } from "./square";

export interface ShipInfoDTO {
    initialSquare: SquareDTO,
    orientation: Orientation,
    size: number
}