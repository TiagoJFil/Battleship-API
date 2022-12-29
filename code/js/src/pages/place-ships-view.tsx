import * as React from "react"
import '../css/game.css'
import { Board } from "../components/entities/board"
import { BoardView } from "../components/board/board-view"
import { Fleet } from "../components/fleet/fleet-view"
import { Timer } from "../components/progress-bar"
import { FleetControls, FleetState } from "../components/fleet/fleet-view"
import { BoardControls } from "../components/board/board-view"
import { CircularProgress } from "@mui/material"


interface PlaceShipViewProps{
    board: Board
    boardControls: BoardControls
    loading: boolean
    fleetState: FleetState
    fleetControls: FleetControls
    layoutDefinitionTimeout: number
    onTimeout: () => void
}

export function PlaceShipView(
    {
        board,
        boardControls, 
        loading,
        layoutDefinitionTimeout,
        fleetState,
        fleetControls,
        onTimeout
    }: PlaceShipViewProps
){
    return !loading ? (
        <section id="layout-definition-view">
            <div className="layout-definition-view-space">
                <div className="fleet-space">
                    <Fleet
                        state={fleetState}
                        controls={fleetControls}
                    />
                </div>
                <div className="boards-space">
                    <BoardView 
                        board={board}
                        controls={boardControls}
                    />
                </div>
                <div className="timer-space">
                    <Timer
                        timeout={layoutDefinitionTimeout}
                        onTimeout={onTimeout}
                    /> 
                </div>
            </div>  
    </section>
    ) :  <CircularProgress />
}