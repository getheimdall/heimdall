import {HTTPv1} from "../utils/Http";

const get = () => {
    return HTTPv1.get('/ldap')
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (ldap) => {
    return HTTPv1.post('/ldap', ldap)
        .then(res => {
            return Promise.resolve(res.data)
        })
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const LdapService = {
    get,
    update
}