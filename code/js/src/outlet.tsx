import * as React from 'react'
import { Outlet } from 'react-router-dom'
import { BottomNav } from './components/bottom-nav'


export function OutletPage() {
    return (
        <div>
            <Outlet/>
            <BottomNav />
        </div>
    )
}