import i18n from "../i18n/i18n"
import { HTTP } from '../utils/Http'
import { HTTPv1 } from '../utils/Http'

const login = (login, password) => {
    let auth = {
        username: login,
        password: password
    }

    return HTTP.get('/v1/index.html', { auth })
    .then(res => {
        localStorage.setItem('user', JSON.stringify(auth))
        return Promise.resolve(auth)
    })
    .catch(error => {
        console.log('Error: ', error)
        throw error;
    })
}

const logout = () => {
    localStorage.removeItem('user')
}

const getUserLocal = () => {
    return JSON.parse(localStorage.getItem('user'))
}

const isUserLogged = () => {
    if (localStorage.getItem('user')) {
        return true
    }
    return false
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