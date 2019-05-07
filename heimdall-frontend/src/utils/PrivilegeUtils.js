const verifyPrivileges = (privilegesAllowed) => {

    if (localStorage.getItem('user') && localStorage.getItem('privileges')) {
        const rolesFromUser = localStorage.getItem('privileges')
        const roles = JSON.parse(rolesFromUser)
        const contains = roles.filter(role => privilegesAllowed.includes(role.name));
        if (contains.length === privilegesAllowed.length) {
            return true
        }
    }
    return false
}

const verifyTypeUser = type => {
    return localStorage.getItem('type') === type
}

export const PrivilegeUtils = {
    verifyPrivileges,
    verifyTypeUser
}