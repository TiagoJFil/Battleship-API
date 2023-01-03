import * as React from 'react'
import { Outlet } from 'react-router-dom'
import { authServices } from '../api/auth'
import { IconLinkButtonList, IconLinkInfo } from '../components/icons'
import { Styles } from '../constants/styles'

import "../css/home.css"

export function Home(){
    const isLoggedIn = authServices.isLoggedIn()

    const authButton : IconLinkInfo = isLoggedIn ?
    {
        title : "Logout",
        iconClass: Styles.LOGOUT_ICON,
        link: '/logout',
        cssTag: "auth"
    } :
    {
        title: 'Login',
        iconClass: Styles.LOGIN_ICON,
        link: '/login',
        cssTag: 'auth'
    }

    const mainIconsButtonList : IconLinkInfo[] = [
        {
            title: "Statistics",
            iconClass: Styles.STATISTICS_ICON,
            link: "/statistics",
            cssTag: "statistics",
        },
        {
            title: "Play",
            iconClass: Styles.PLAY_ICON,
            link: "/lobby",
            cssTag: "play",
        },
        {
            title: "my games",
            iconClass: Styles.HOME_ICON,
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

