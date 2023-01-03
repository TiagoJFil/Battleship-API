import { BottomNavigation , BottomNavigationAction } from '@mui/material';
import HomeIcon from '@mui/icons-material/Home';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import InfoIcon from '@mui/icons-material/Info';
import * as React from "react"
import { useNavigate } from 'react-router-dom';
import "./../css/bottom-nav.css"
import { Styles } from '../constants/styles';
import VideoLibraryIcon from '@mui/icons-material/VideoLibrary';

export function BottomNav(){
    const navigate = useNavigate();
    const styles = {
        backgroundColor: "transparent"
    }

    return (
        <BottomNavigation className={Styles.BOTTOM_BAR_CONTAINER} style={styles} showLabels>
            <BottomNavigationAction className={Styles.BOTTOM_BAR_BUTTON} label="Back" icon={<ArrowBackIcon />} onClick={() => navigate(-1) } />
            <BottomNavigationAction className={Styles.BOTTOM_BAR_BUTTON} label="Home" icon={<HomeIcon />} onClick={() => navigate("/") } />
            <BottomNavigationAction className={Styles.BOTTOM_BAR_BUTTON} label="My games" icon={<VideoLibraryIcon />} onClick={() => navigate("/my/games") } />
        </BottomNavigation>
    )
}