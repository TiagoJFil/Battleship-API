import { Orientation } from "../../components/utils/orientation";
import { SquareDTO } from "./square";

export interface ShipInfo {
    initialSquare: SquareDTO,
    size: number,
    orientation: Orientation
}