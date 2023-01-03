import * as React from 'react';
import { Styles } from '../constants/styles';
import { GameConstants } from '../constants/game';
import { emphasize } from '@mui/material';

const ABOVE_THRESHOLD_COLOR = (color: BarColor) => {
    switch (color) {
        case BarColor.PRIMARY:
        return PRIMARY_COLOR
        case BarColor.SECONDARY:
        return SECONDARY_COLOR
    }
}
const BELOW_THRESHOLD_COLOR = 'var(--oc-red-5)'
const PRIMARY_COLOR = 'var(--oc-blue-5)'
const SECONDARY_COLOR = 'var(--oc-violet-5)'

interface CustomProgressBarProps {
    progress: number,
    color: BarColor
}

export enum BarColor {
    PRIMARY="primary", SECONDARY="secondary"
}

export function CustomProgressBar({ progress, color }: CustomProgressBarProps){

    const backgroundColor = 
        progress > GameConstants.PROGRESS_WARNING_THRESHOLD_PERCENTAGE ? ABOVE_THRESHOLD_COLOR(color) : BELOW_THRESHOLD_COLOR

    const fillerStyles = {
        height: '100%',
        width: `${progress}%`,
        backgroundColor,
        borderRadius: 'inherit',
        transition: 'width 1s ease-in-out',
      }

    return(
        <div className={Styles.PROGRESS_BAR_CONTAINER}>
            <div style={fillerStyles}> </div>
        </div>
    )
    
}