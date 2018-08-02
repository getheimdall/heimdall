import axios from 'axios'

const baseURL = process.env.REACT_APP_SCHEME + '://' + process.env.REACT_APP_ADDRESS + ':' + process.env.REACT_APP_PORT

const HTTP = axios.create({
    baseURL: baseURL
})

const HTTPv1 = axios.create({
    baseURL: baseURL + process.env.REACT_APP_API,
    headers: {'Content-Type': 'application/json'}
})

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
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        localStorage.removeItem('privileges')
        window.location.pathname = '/login'
    }

    return Promise.reject(error)
})

export {HTTP, HTTPv1};