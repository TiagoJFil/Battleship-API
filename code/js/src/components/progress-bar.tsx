import * as React from 'react';
import { Styles } from '../constants/styles';
import { GameConstants } from '../constants/game';

const ABOVE_THRESHOLD_COLOR = 'var(--oc-blue-5)'
const BELOW_THRESHOLD_COLOR = 'var(--oc-red-5)'

interface CustomProgressBarProps {
    progress: number
}

export function CustomProgressBar({ progress }: CustomProgressBarProps){

    const backgroundColor = 
        progress > GameConstants.PROGRESS_WARNING_THRESHOLD_PERCENTAGE ? ABOVE_THRESHOLD_COLOR : BELOW_THRESHOLD_COLOR

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