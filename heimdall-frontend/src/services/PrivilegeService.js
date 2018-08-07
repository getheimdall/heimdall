import { HTTPv1 }from '../utils/Http'

const getPrivilegesByUsername = (username) => {
    return HTTPv1.get('/privileges/username/' + username)
        .then(res => {
            localStorage.setItem('privileges', JSON.stringify(res.data))
            Promise.resolve(res.data)
        })
        .catch(error => {
            localStorage.clear()
            console.log('Error: ', error)
            throw error
        });
}

export const privilegeService = {
    getPrivilegesByUsername
}