import * as React from 'react'
import { Link, Outlet, useNavigate } from 'react-router-dom'
import { isLoggedIn } from '../../api/session'
import { BottomNav } from '../bottomNav'
import { styles } from '../../styles'
import { IconButton } from '../icons'

import "./home.css"

export function Home(){
    const navigate = useNavigate();

    return(
    
        <div>
        <h1>Home</h1>
        <nav>
            <li><Link to="/statistics">Statistics</Link></li>
            <li><Link to="/system-info">System Information</Link></li>
            <li><Link to="/logout">Logout</Link></li>
            <li><Link to="/lobby">Join Queue</Link></li>
        </nav>

        <div className="play-button-container">
            <IconButton iconClass={styles.PLAY_ICON} onClick={() => navigate("/lobby") } />
        </div>
        <Outlet/>
        <BottomNav />
    </div>)
}

