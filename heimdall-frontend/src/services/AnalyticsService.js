import { HTTPv1 } from "../utils/Http"

const getAppsTop = (limit, period) => {
    const params = {
        params: {
            limit: limit,
            period: period
        }
    }
    return HTTPv1.get('/metrics/apps/top', params)
        .then(result => {
           return Promise.resolve(result.data)
        })
        .catch(error => {
            console.log(error)
            return Promise.reject(error)
        })
}

const getApisTop = (limit, period) => {
    const params = {
        params: {
            limit: limit,
            period: period
        }
    }
    return HTTPv1.get('/metrics/apis/top', params)
        .then(result => {
            return Promise.resolve(result.data)
        })
        .catch(error => {
            console.log(error)
            return Promise.reject(error)
        })
}

const getAccessTokensTop = (limit, period) => {
    const params = {
        params: {
            limit: limit,
            period: period
        }
    }
    return HTTPv1.get('/metrics/access-tokens/top', params)
        .then(result => {
            return Promise.resolve(result.data)
        })
        .catch(error => {
            console.log(error)
            return Promise.reject(error)
        })
}

const getResultStatusTop = (limit, period) => {
    const params = {
        params: {
            limit: limit,
            period: period
        }
    }
    return HTTPv1.get('/metrics/result-status/top', params)
        .then(result => {
            return Promise.resolve(result.data)
        })
        .catch(error => {
            console.log(error)
            return Promise.reject(error)
        })
}

export const analyticsService = {
    getAppsTop,
    getApisTop,
    getAccessTokensTop,
    getResultStatusTop
}