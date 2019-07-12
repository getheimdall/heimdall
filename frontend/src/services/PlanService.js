import i18n from "../i18n/i18n"
import { HTTPv1 } from '../utils/Http'

const getPlans = (params = {params: {}}) => {
    return HTTPv1.get('/plans', params)
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

const getPlan = (planId) => {
    if (isNaN(planId)) {
        return Promise.reject(new Error(i18n.t('invalid_parameter')))
    }

    return HTTPv1.get('/plans/' + planId)
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

const save = (plan) => {
    return HTTPv1.post('/plans', JSON.stringify(plan))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const update = (plan) => {
    return HTTPv1.put('/plans/' + plan.id, JSON.stringify(plan))
        .then(res => Promise.resolve(res.data))
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

const remove = (planId) => {
    return HTTPv1.delete('/plans/' + planId)
        .catch(error => {
            console.log('Error: ', error)
            if (error.response && error.response.status === 404) {
                return null;
            }
            throw error;
        })
}

export const planService = {
    getPlans,
    getPlan,
    save,
    update,
    remove
}