import axios from 'axios'

const baseURL = process.env.REACT_APP_SCHEME + '://' + process.env.REACT_APP_ADDRESS + ':' + process.env.REACT_APP_PORT

const HTTP = axios.create({
    baseURL: baseURL
})

const HTTPv1 = axios.create({
    baseURL: baseURL + process.env.REACT_APP_API,
    headers: {'Content-Type': 'application/json'}
})

axios.defaults.paramsSerializer = params => {
    return serializerParams(params)
}

HTTPv1.interceptors.request.use(req => {
    if (localStorage.getItem('token')) {
        const token = localStorage.getItem('token');
        req.headers.Authorization = `Bearer ${token}`
    }
    return req
}, error => Promise.reject(error))

HTTPv1.interceptors.response.use(res => {
    const token = res.headers.authorization
    localStorage.setItem('token', token)
    return res
}, error => {
    const response = error.response
    if (response.status === 401 || response.status === 403 || response.data.message === "Token expired") {
        localStorage.clear()
        window.location.pathname = '/login'
    }

    return Promise.reject(error)
})

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