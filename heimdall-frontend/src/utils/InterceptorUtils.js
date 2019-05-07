import React from 'react'
import ClientId from '../components/interceptors/wrappers/ClientId'
import Mock from '../components/interceptors/wrappers/Mock'
import Ratting from '../components/interceptors/wrappers/Ratting'
import Ips from '../components/interceptors/wrappers/Ips'
import Cache from '../components/interceptors/wrappers/Cache'
import CacheClear from '../components/interceptors/wrappers/CacheClear'
import Cors from '../components/interceptors/wrappers/Cors'
import Default from '../components/interceptors/wrappers/Default'
import AccessToken from '../components/interceptors/wrappers/AccessToken'
import OAuth from '../components/interceptors/wrappers/OAuth'
import Identifier from '../components/interceptors/wrappers/Identifier'
import LogMasker from '../components/interceptors/wrappers/LogMasker'
import { InterceptorContent } from './InterceptorContentUtils'

export const TEMPLATES = (form, content) => ({
    ACCESS_TOKEN: <AccessToken form={form} content={content}/>,
    CLIENT_ID: <ClientId form={form} content={content} />,
    MOCK: <Mock form={form} content={content} />,
    RATTING: <Ratting form={form} content={content} />,
    BLACKLIST: <Ips form={form} content={content} />,
    WHITELIST: <Ips form={form} content={content} />,
    CACHE: <Cache form={form} content={content} />,
    CACHE_CLEAR: <CacheClear form={form} content={content} />,
    CORS: <Cors form={form} content={content} />,
    CUSTOM: <Default form={form} content={content}/>,
    LOG_MASKER: <LogMasker form={form} content={content}/>,
    OAUTH: <OAuth form={form} content={content}/>,
    IDENTIFIER: <Identifier form={form} content={content}/>,
    MIDDLEWARE: <Default form={form} content={content}/>,
})

export const CONTENTS = (content, type) => {

    switch (type) {
        case 'BLACKLIST':
        case 'WHITELIST':
            return InterceptorContent.ipsContent(content)
        case 'CACHE':
            return InterceptorContent.cacheContent(content)
        case 'LOG_MASKER':
            return InterceptorContent.logMaskerContent(content)
        default:
            return InterceptorContent.defaultContent(content)
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