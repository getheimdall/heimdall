import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getEnvironments = () => {
    return HTTPv1.get('/environments')
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

const getEnvironment = (idEnvironment) => {
    if (isNaN(idEnvironment)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/environments/' + idEnvironment)
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

const save = (environment) => {
    return HTTPv1.post('/environments', JSON.stringify(environment))
    .then(res => Promise.resolve(res.data))
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return null;
        }
        throw error;
    })
}

const update = (environment) => {
    return HTTPv1.put('/environments/'+environment.id, JSON.stringify(environment))
    .then(res => Promise.resolve(res.data))
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return null;
        }
        throw error;
    })
}

const remove = (environmentId) => {
    return HTTPv1.delete('/environments/'+environmentId)
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return null;
        }
        throw error;
    })
}



export const environmentService = {
    getEnvironments,
    getEnvironment,
    save,
    update,
    remove
}