import * as React from 'react'
import { useNavigate } from 'react-router-dom';
import { isLoggedIn } from '../api/session';
import { styles } from '../styles';
import './bottomNav.css'
import { IconButton } from './icons';



export function BottomNav() {
    const navigate = useNavigate();

    const authButton = isLoggedIn() ? {
        icon: styles.LOGOUT_ICON,
        link: '/logout',
    } :
    {
        icon: styles.LOGIN_ICON,
        link: '/login',
    } 

    return (
        <div className='Bottom-Nav-Outter'>
            <div className='Bottom-Nav-Inner'>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={styles.INFO_ICON} onClick={() => navigate("/info") } />
                </div>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={styles.HOME_ICON} onClick={() => navigate("/") } />
                </div>
                <div className='Bottom-Nav-Item'>
                    <IconButton iconClass={authButton.icon} onClick={() => navigate(authButton.link) } />
                </div>
            </div>
        </div>
    )    
}

    