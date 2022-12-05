const passwordRules = [
    {
        message: "Password must be at least 5 characters long",
        validate: (value: string) => value.length >= 5
    },
    {
        message: "Password must contain at least one digit",
        validate: (value: string) => /\d/.test(value)
    },
] 

const usernameRules = [
    {
        message: "Username must be at least 3 characters long",
        validate: (value: string) => value.length >= 3
    },
    {
        message: "Username must be at most 30 characters long",
        validate: (value: string) => value.length <= 30
    },
]

export function validateAuth(username: string, password: string) : boolean {
    usernameRules.forEach( rule => {
        if(!rule.validate(username) ){
            throw {
                title: rule.message
            }
        }
    })

    passwordRules.forEach( rule => {
        if( !rule.validate(password) ){
            throw {
                title: rule.message  
            }
        }
    })

    return true
}