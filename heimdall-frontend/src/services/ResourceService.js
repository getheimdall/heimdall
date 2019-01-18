import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getResourcesByApi = (idApi) => {

    if (isNaN(idApi)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + idApi + '/resources')
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const getResource = (idApi, id) => {
    if (isNaN(idApi) || isNaN(id)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/apis/' + idApi + '/resources/' + id)
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const save = (idApi, resource) => {
    if (isNaN(idApi)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.post('/apis/' + idApi + '/resources', JSON.stringify(resource))
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)

        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const update = (idApi, resource) => {
    if (isNaN(idApi)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.put('/apis/' + idApi + '/resources/'+ resource.id, JSON.stringify(resource))
    .then(res => {

        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)

        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

const remove = (idApi, idResource) => {
    if (isNaN(idApi) || isNaN(idResource)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.delete('/apis/' + idApi + '/resources/'+ idResource)
    .then(res => {
        return Promise.resolve(res.data)
    })
    .catch(error => {
        console.log('Error: ', error)
        if (error.response && error.response.status === 404) {
            return Promise.reject(new Error(i18n.t('resource_not_found')));
        }
        throw error;
    })
}

export const resourceService = {
    getResourcesByApi,
    getResource,
    save,
    update,
    remove
}