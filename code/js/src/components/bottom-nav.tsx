import * as React from 'react'
import { useNavigate } from 'react-router-dom';
import { Styles } from '../constants/styles';
import './../css/bottom-nav.css'
import { IconButton } from './icons';



export function BottomNav() {
    const navigate = useNavigate();

    return (
        <div className='Bottom-Nav-Outter'>
            <div className='Bottom-Nav-Inner'>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={Styles.BACK_ICON} onClick={() => navigate(-1) } />
                </div>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={Styles.HOME_ICON} onClick={() => navigate("/") } />
                </div>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={Styles.INFO_ICON} onClick={() => navigate("/info") } />
                </div>
            </div>
        </div>
    )    
}

    