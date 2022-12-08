export const TOAST_IN_BETWEEN_DELAY = 300


/**
 * Toast that gives an error message
 */
export function ErrorToast(message) {
    return Toastify({
        text: message,
        backgroundColor: "linear-gradient(to right, #ff6c6c, #f66262)",
        oldestFirst: false,
    })     
}

/**
 * Toast that gives a succesfull message
 */
export function SuccessToast(message) {
    return Toastify({
        text: message,
        backgroundColor: "linear-gradient(to right, #6eff99, #66ff66)",
        oldestFirst: false,
    })     
}

/**
 * Toast that gives an informational message
 */
export function InfoToast(message) {
    return Toastify({
        text: message,
        backgroundColor: "linear-gradient(to right, #66b3ff, #6699ff)",
        oldestFirst: false,
    })
}

export function showToasts(toasts) {
    toasts.forEach((toast, i) =>
        setTimeout(() => {
            toast.showToast()
        }, TOAST_IN_BETWEEN_DELAY * i)
    )
}

