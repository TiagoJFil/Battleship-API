import * as React from 'react'
import { Link, Outlet, useNavigate } from 'react-router-dom'
import { authServices } from '../api/auth'
import { BottomNav } from '../components/bottom-nav'
import { IconLinkButtonList, IconLinkInfo } from '../components/icons'
import { styles } from '../constants/styles'

import "../css/home.css"

export function Home(){

    const authButton : IconLinkInfo = authServices.isLoggedIn() ?
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
        authButton
    ]


    return(
        <div>
        <h1>Home</h1>
            <IconLinkButtonList icons={mainIconsButtonList}/>
        <Outlet/>
        <BottomNav />
    </div>)
}

