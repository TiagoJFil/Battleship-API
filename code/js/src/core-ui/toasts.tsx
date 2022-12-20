import { toast, ToastOptions } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const toastConfig : ToastOptions = {
    position: "top-right",
    autoClose: 5000,
    hideProgressBar: false,
    closeOnClick: true,
    pauseOnHover: true,
    draggable: true,
    progress: undefined,
    theme: "light",
}

/**
 * Toast that gives an error message
 */
export function ErrorToast(message) {
    return toast.error(message,toastConfig)     
}

/**
 * Toast that gives a succesfull message
 */
export function SuccessToast(message) {
    return toast.success(message,toastConfig)     
}

/**
 * Toast that gives an informational message
 */
export function InfoToast(message) {
    return toast.info(message,toastConfig)
}


