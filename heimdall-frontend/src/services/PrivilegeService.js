import { HTTPv1 }from '../utils/Http'

const getAllPrivileges = () => {
    return HTTPv1.get('/privileges')
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error
        })
}

const getPrivilegesByUsername = (username) => {
    return HTTPv1.post('/privileges/username/', username)
        .then(res => {
            localStorage.setItem('privileges', JSON.stringify(res.data))
            return Promise.resolve(res.data)
        })
        .catch(error => {
            localStorage.clear()
            console.log('Error: ', error)
            if (error.response && error.response.status === 404){
                return null
            }
            throw error
        });
}

const getPrivilege = (privilegeId) => {
    return HTTPv1.get(`privileges/${privilegeId}`)
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error
        })
}

export const privilegeService = {
    getPrivilegesByUsername,
    getAllPrivileges,
    getPrivilege
}