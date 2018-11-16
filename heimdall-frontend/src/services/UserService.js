import i18n from "../i18n/i18n"
import {JwtUtils} from "../utils/JwtUtils"
import {HTTP, HTTPv1} from '../utils/Http'

const login = (login, password) => {
    let accountCredentials = {
        username: login,
        password: password
    }

    return HTTP.post('/v1/api/login', accountCredentials)
        .then(res => {
            const token = res.headers.authorization
            const user = JwtUtils.decodePayloadAsJson(token).sub
            localStorage.setItem('token', token)
            localStorage.setItem('user', user)
            return Promise.resolve(accountCredentials)
        })
        .catch(error => {
            console.log('Error: ', error)
            throw error;
        })
}

const logout = () => {
    if (localStorage.getItem('token')) {
        const headers = {'Authorization': localStorage.getItem('token')}
        HTTP.get('/v1/api/logout', {headers}).then(res => {
            localStorage.clear()
        });
    }
}

const getUserLocal = () => {
    return {username: localStorage.getItem('user')};
}

const isUserLogged = () => {
    return !!localStorage.getItem('token');
}

const getUsers = (params = {params: {}}) => {
    return HTTPv1.get('/users', params)
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

const getUser = (userId) => {
    if (isNaN(userId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/users/' + userId)
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

const save = (user) => {
    return HTTPv1.post('/users', JSON.stringify(user))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (user) => {
    return HTTPv1.put('/users/' + user.id, JSON.stringify(user))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (userId) => {
    return HTTPv1.delete('/users/' + userId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const userService = {
    login,
    logout,
    getUserLocal,
    isUserLogged,
    remove,
    update,
    save,
    getUser,
    getUsers
}