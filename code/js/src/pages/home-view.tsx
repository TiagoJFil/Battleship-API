import * as React from 'react'
import { Outlet } from 'react-router-dom'
import { authServices } from '../api/auth'
import { IconLinkButtonList, IconLinkInfo } from '../components/icons'
import PlayCircleIcon from '@mui/icons-material/PlayCircle';
import InfoIcon from '@mui/icons-material/Info';
import LoginIcon from '@mui/icons-material/Login';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import LogoutIcon from '@mui/icons-material/Logout';
import { Typography } from "@mui/material";
import "../css/home.css"

export function Home(){
    const isLoggedIn = authServices.isLoggedIn()

    const authButton : IconLinkInfo = isLoggedIn ?
    {
        title : "Logout",
        icon: LogoutIcon,
        link: '/logout',
        cssTag: "auth"
    } :
    {
        title: 'Login',
        icon: LoginIcon,
        link: '/login',
        cssTag: 'auth'
    }

    const mainIconsButtonList : IconLinkInfo[] = [
        {
            title: "Statistics",
            icon: LeaderboardIcon,
            link: "/statistics",
            cssTag: "statistics",
        },
        {
            title: "Play",
            icon: PlayCircleIcon,
            link: "/lobby",
            cssTag: "play",
        },
        {
            title: "Information",
            icon: InfoIcon,
            link: "/info",
            cssTag: "info",
        },
        authButton
    ]

    return(
        <div className='home-page'>
            <div className='page-title'>
            <Typography className='app-title' align='center' variant="h1">Battleship</Typography>
            </div>
            <IconLinkButtonList icons={mainIconsButtonList}/>
            <Outlet/>
        </div>
    )
}

