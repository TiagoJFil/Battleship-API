import { Fade, Modal } from "@mui/material"
import Button from '@mui/material/Button';
import * as React from "react"
import { styles } from "../constants/styles"
import '../css/modal.css'

export default function AnimatedModal(
    props: {
        message: string
        show: boolean
    }
){

    const [open, setOpen] = React.useState(props.show)
    const handleClose = () => setOpen(false)


    return(
        <div>
            <Modal
                aria-labelledby="transition-modal-title"
                aria-describedby="transition-modal-description"
                className={styles.MODAL_CONTAINER}
                open={open}
                onClose={handleClose}
                closeAfterTransition
            >
                <Fade in={open}>
                    <div className={styles.MODAL}>
                            <div>
                                {props.message}
                            </div>
                            <Button variant="contained" color="success" onClick={handleClose}>
                                Return
                            </Button>
                    </div>
                </Fade>
            </Modal>
        </div>  
    )
}

