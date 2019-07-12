import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getDevelopers = (params = { params: {} }) => {
    return HTTPv1.get('/developers', params)
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

const getDeveloper = (idDeveloper) => {
    if (isNaN(idDeveloper)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/developers/' + idDeveloper)
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

const save = (developer) => {
    return HTTPv1.post('/developers', JSON.stringify(developer))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (developer) => {
    return HTTPv1.put('/developers/' + developer.id, JSON.stringify(developer))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (developerId) => {
    return HTTPv1.delete('/developers/' + developerId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const developerService = {
    getDevelopers,
    getDeveloper,
    save,
    update,
    remove
}