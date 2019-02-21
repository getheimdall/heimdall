export const TEMPLATE_ACCESS_TOKEN = "{\"location\": \"HEADER\", \"name\": \"access_token\"}";
export const TEMPLATE_MOCK = "{\"body\": \"{'name': 'Mock Example'}\", \"status\": \"200\"}";
export const TEMPLATE_RATTING = "{\"calls\":20,\"interval\":\"MINUTES\"}";
export const TEMPLATE_IPS = "{\"ips\": [ \"127.0.0.0\", \"127.0.0.1\" ]}";
export const TEMPLATE_CACHE = "{\"cache\":\"cache-name\", \"timeToLive\": 10000, \"headers\": [\"header1\", \"header2\"], \"queryParams\": [\"queryParam1\", \"queryParam2\"]}";
export const TEMPLATE_CACHE_CLEAR = "{\"cache\":\"cache-name\"}";
export const TEMPLATE_IDENTIFIER = "{}";
export const TEMPLATE_CORS = "{\"Access-Control-Allow-Origin\": \"*\", " +
    "\"Access-Control-Allow-Credentials\": \"true\", " +
    "\"Access-Control-Allow-Methods\": \"POST, GET, PUT, PATCH, DELETE, OPTIONS\", " +
    "\"Access-Control-Allow-Headers\": \"origin, content-type, accept, authorization, x-requested-with, X-AUTH-TOKEN, access_token, client_id, device_id, credential\", " +
    "\"Access-Control-Max-Age\": \"3600\"}";

export const getTemplate = (type) => {
    if (type === 'ACCESS_TOKEN') {
        return TEMPLATE_ACCESS_TOKEN
    }

    if (type === 'CLIENT_ID') {
        return TEMPLATE_ACCESS_TOKEN
    }

    if (type === 'MOCK') {
        return TEMPLATE_MOCK
    }

    if (type === 'RATTING') {
        return TEMPLATE_RATTING
    }

    if (type === 'BLACKLIST' || type === 'WHITELIST'){
        return TEMPLATE_IPS
    }

    if (type === 'CACHE') {
        return TEMPLATE_CACHE
    }

    if (type === 'CACHE_CLEAR') {
        return TEMPLATE_CACHE_CLEAR
    }

    if (type === 'IDENTIFIER'){
        return TEMPLATE_IDENTIFIER
    }

    if (type === 'CORS') {
        return TEMPLATE_CORS
    }

}

export const interceptorSort = (first, second) => {

    if (first.lifeCycle === 'API' && second.lifeCycle !== 'API') {
        return -1
    }

    if (first.lifeCycle !== 'API' && second.lifeCycle === 'API') {
        return 1
    }

    if (first.lifeCycle === 'API' && second.lifeCycle === 'API') {
        if (first.order < second.order) return -1
        if (first.order > second.order) return 1
    }

    if (first.lifeCycle === 'PLAN' && second.lifeCycle !== 'PLAN') {
        return -1
    }

    if (first.lifeCycle !== 'PLAN' && second.lifeCycle === 'PLAN') {
        return 1
    }

    if (first.lifeCycle === 'PLAN' && second.lifeCycle === 'PLAN') {
        if (first.order < second.order) return -1
        if (first.order > second.order) return 1
    }

    if (first.lifeCycle === 'RESOURCE' && second.lifeCycle !== 'RESOURCE') {
        return -1
    }

    if (first.lifeCycle !== 'RESOURCE' && second.lifeCycle === 'RESOURCE') {
        return 1
    }

    if (first.lifeCycle === 'RESOURCE' && second.lifeCycle === 'RESOURCE') {
        if (first.order < second.order) return -1
        if (first.order > second.order) return 1
    }

    if (first.lifeCycle === 'OPERATION' && second.lifeCycle !== 'OPERATION') {
        return -1
    }

    if (first.lifeCycle !== 'OPERATION' && second.lifeCycle === 'OPERATION') {
        return 1
    }

    if (first.lifeCycle === 'OPERATION' && second.lifeCycle === 'OPERATION') {
        if (first.order < second.order) return -1
        if (first.order > second.order) return 1
    }

    return 0
}