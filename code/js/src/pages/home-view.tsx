import * as React from 'react'
import { Outlet } from 'react-router-dom'
import { authServices } from '../api/auth'
import { IconLinkButtonList, IconLinkInfo } from '../components/icons'
import { styles } from '../constants/styles'

import "../css/home.css"

export function Home(){
    const isLoggedIn = authServices.isLoggedIn()

    const authButton : IconLinkInfo = isLoggedIn ?
    {
        title : "Logout",
        iconClass: styles.LOGOUT_ICON,
        link: '/logout',
        cssTag: "auth"
    } :
    {
        title: 'Login',
        iconClass: styles.LOGIN_ICON,
        link: '/login',
        cssTag: 'auth'
    }

    const mainIconsButtonList : IconLinkInfo[] = [
        {
            title: "Statistics",
            iconClass: styles.STATISTICS_ICON,
            link: "/statistics",
            cssTag: "statistics",
        },
        {
            title: "Play",
            iconClass: styles.PLAY_ICON,
            link: "/lobby",
            cssTag: "play",
        },
        {
            title: "my games",
            iconClass: styles.HOME_ICON,
            link: "/my/games",
            cssTag: "my-games",
        },
        authButton
    ]

    return(
        <div className='home-page'>
            <div className='page-title'>
                <span className='app-title'>Battleship</span>
            </div>
            <IconLinkButtonList icons={mainIconsButtonList}/>
            <Outlet/>
        </div>
    )
}

