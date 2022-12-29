import { Modal } from "@mui/material"
import Button from '@mui/material/Button';
import * as React from "react"
import { styles } from "../constants/styles"
import '../css/modal.css'

export default function AnimatedModal(
    props: {
        message: string
        show: boolean
        handleClose?: () => void
    }
){
    return(
        <Modal
            open={props.show}
            onClose={props.handleClose}
        >
            <div className={styles.MODAL}>
                <div>{props.message}</div>
                <Button variant="contained" color="success" onClick={props.handleClose}>
                    Return
                </Button>
            </div>
        </Modal>
    )
}

