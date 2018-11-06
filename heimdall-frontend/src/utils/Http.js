import axios from 'axios'

const HTTP = axios.create({
     baseURL: process.env.REACT_APP_SCHEME + '://' + process.env.REACT_APP_ADDRESS + ':' + process.env.REACT_APP_PORT,
})

const HTTPv1 = axios.create({
    baseURL: process.env.REACT_APP_SCHEME + '://' + process.env.REACT_APP_ADDRESS + ':' + process.env.REACT_APP_PORT + process.env.REACT_APP_API,
    headers: {'Content-Type': 'application/json'}
})

axios.defaults.paramsSerializer = params => {
    return serializerParams(params)
}

HTTPv1.interceptors.request.use(req => {
    if (localStorage.getItem('user')) {
        req.auth = JSON.parse(localStorage.getItem('user'))
    }
     return req
}, error => Promise.reject(error))

HTTPv1.interceptors.response.use(res => {
    if (res.status === 401) {
        localStorage.removeItem('user')
    }
    return res
}, error => Promise.reject(error))

function serializerParams(params, keyConcat) {

    return Object.entries(params)
        .filter(([key, value]) => value !== undefined && value !== null)
        .map(([key, value]) => {
            let result = ''
            if (keyConcat) {
                result = keyConcat + '.'
            }
            if (typeof value === 'object') {
                return result.concat(serializerParams(value, key))
            } else {
                return result.concat(`${key}=${value}`)
            }
        }).join('&')
}

export {HTTP, HTTPv1};