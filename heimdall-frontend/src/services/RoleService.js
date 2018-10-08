import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getRoles = () => {
    return HTTPv1.get('/roles')
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

const getRole = (roleId) => {
    if (isNaN(roleId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/roles/' + roleId)
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

const save = (role) => {
    return HTTPv1.post('/roles', JSON.stringify(role))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (role) => {
    return HTTPv1.put('/roles/' + role.id, JSON.stringify(role))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (roleId) => {
    return HTTPv1.delete('/roles/' + roleId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const roleService = {
    getRoles,
    getRole,
    save,
    update,
    remove
}