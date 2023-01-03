import * as React from 'react'
import { Styles } from '../constants/styles';
import { useNavigate } from 'react-router-dom'


export function IconButton(prop : {id?: string, title?: string, iconClass: string, onClick: () => void}){
    return (
        <button id={prop.id} title={prop.title} className="icon-button" onClick={prop.onClick}>
            <BoxIcon className ={prop.iconClass}/>
        </button>
    )
}

export function BoxIcon(prop : {className: string}){
    const className = Styles.BX_CLASS + ' ' + prop.className;
    return ( <i className={className}/>)
}


export interface IconLinkInfo {
    title: string;
    iconClass: string;
    link: string;
    cssTag: string;
}


export function IconLinkButtonList(prop : {icons: IconLinkInfo[]}){
    const navigate = useNavigate();
    const icons : IconLinkInfo[] = prop.icons

    return (
        <div className="icon-list-container">
            <div className="icon-list">
                {icons.map((icon, index) => {
                    const containerClassName = `${icon.cssTag}-button-container`;
                    return (
                    <div key={index} className={containerClassName} >
                        <IconButton key={index} title={icon.title} iconClass={icon.iconClass} onClick={() => navigate(icon.link) } />
                    </div>
                    )
                })}
            </div>
        </div>
    )
}