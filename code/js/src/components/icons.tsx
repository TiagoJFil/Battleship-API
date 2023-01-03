import * as React from 'react'
import { Styles } from '../constants/styles';
import { useNavigate } from 'react-router-dom'
import { OverridableComponent } from '@mui/material/OverridableComponent';
import { SvgIconTypeMap } from '@mui/material';


export function IconButton(prop : {id?: string, title?: string, icon: OverridableComponent<SvgIconTypeMap<{}, "svg">> & {
    muiName: string;
} , onClick: () => void}){
    return (
        <button id={prop.id} title={prop.title} className="icon-button" onClick={prop.onClick}>
            <div className="icon-container">
                <prop.icon/>
            </div>
        </button>
    )
}

export interface IconLinkInfo {
    title: string;
    icon: OverridableComponent<SvgIconTypeMap<{}, "svg">> & {
        muiName: string;
    };
    link: string;
    cssTag: string;
}


export function IconLinkButtonList(prop : {icons: IconLinkInfo[]}){
    const navigate = useNavigate();
    const icons : IconLinkInfo[] = prop.icons

    return (
        <div className="icon-list-container">
            <div className="icon-list">
                {icons.map((iconInfo, index) => {
                    const containerClassName = `${iconInfo.cssTag}-button-container`;
                    return (
                    <div key={index} className={containerClassName} >
                        <IconButton key={index} title={iconInfo.title} icon={iconInfo.icon} onClick={() => navigate(iconInfo.link) } />
                    </div>
                    )
                })}
            </div>
        </div>
    )
}