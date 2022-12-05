import { Orientation } from "../../components/utils/orientation";
import { Square } from "./square";

export interface ShipInfo {
    initialSquare: Square,
    size: number,
    orientation: Orientation
}