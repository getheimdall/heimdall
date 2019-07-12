import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getApps = (params = {params: {}}) => {
    return HTTPv1.get('/apps', params)
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

const getApp = (appId) => {
    if (isNaN(appId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apps/' + appId)
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

const save = (app) => {
    return HTTPv1.post('/apps', JSON.stringify(app))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (app) => {
    return HTTPv1.put('/apps/' + app.id, JSON.stringify(app))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (appId) => {
    return HTTPv1.delete('/apps/' + appId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const appService = {
    getApps,
    getApp,
    save,
    update,
    remove
}