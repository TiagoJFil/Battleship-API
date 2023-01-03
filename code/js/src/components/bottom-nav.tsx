import { BottomNavigation , BottomNavigationAction } from '@mui/material';
import HomeIcon from '@mui/icons-material/Home';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import InfoIcon from '@mui/icons-material/Info';
import * as React from "react"
import { useNavigate } from 'react-router-dom';
import "./../css/bottom-nav.css"

export function BottomNav(){
    const navigate = useNavigate();

    return (
        <div className='Bottom-Nav-Outter'>
            <BottomNavigation showLabels>
                <BottomNavigationAction label="Favorites" icon={<ArrowBackIcon />} onClick={() => navigate(-1) } />
                <BottomNavigationAction label="Recents" icon={<HomeIcon />} onClick={() => navigate("/") } />
                <BottomNavigationAction label="Nearby" icon={<InfoIcon />} onClick={() => navigate("/info") } />
            </BottomNavigation>
        </div>
    )
}