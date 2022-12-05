import * as React from 'react'
import { styles } from '../styles';
import { useNavigate } from 'react-router-dom'


export function IconButton(prop){
    return (
        <button id={prop.id} title={prop.title} className="icon-button" onClick={prop.onClick}>
            <BoxIcon className ={prop.iconClass}/>
        </button>
    )
}

export function BoxIcon(prop){
    const className = styles.BX_CLASS + ' ' + prop.className;
    return ( <i className={className}/>)
}


export interface IconLinkInfo {
    title: string;
    iconClass: string;
    link: string;
}


function IconLinkButtonList(...icons : IconLinkInfo[]){
    const navigate = useNavigate();

    return (
        <div className="icon-list">
            {icons.map((icon, index) => {
                const containerClassName = `${icon.title}-button-container`;
                return (
                <div className={containerClassName} > 
                    <IconButton key={index} title={icon.title} iconClass={icon.iconClass} onClick={() => navigate(icon.link) } />~
                </div>
                )
            })}
        </div>
    )



}