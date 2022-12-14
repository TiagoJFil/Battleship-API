import * as React from 'react'
import { useNavigate } from 'react-router-dom';
import { authServices } from '../api/auth';
import { styles } from '../constants/styles';
import './bottom-nav.css'
import { IconButton } from './icons';



export function BottomNav() {
    const navigate = useNavigate();

    return (
        <div className='Bottom-Nav-Outter'>
            <div className='Bottom-Nav-Inner'>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={styles.BACK_ICON} onClick={() => navigate(-1) } />
                </div>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={styles.HOME_ICON} onClick={() => navigate("/") } />
                </div>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={styles.INFO_ICON} onClick={() => navigate("/info") } />
                </div>
            </div>
        </div>
    )    
}

    