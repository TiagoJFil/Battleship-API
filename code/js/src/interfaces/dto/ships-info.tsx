import { Orientation } from "../../components/entities/orientation";
import { SquareDTO } from "./square";

export interface ShipInfoDTO {
    initialSquare: SquareDTO,
    size: number,
    orientation: Orientation
}