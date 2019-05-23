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

export const TEMPLATES = (form, content, type) => ({
    ACCESS_TOKEN: <AccessToken form={form} content={parseContentByType(content, type)}/>,
    CLIENT_ID: <ClientId form={form} content={parseContentByType(content, type)} />,
    MOCK: <Mock form={form} content={parseContentByType(content, type)} />,
    RATTING: <Ratting form={form} content={parseContentByType(content, type)} />,
    BLACKLIST: <Ips form={form} content={parseContentByType(content, type)} />,
    WHITELIST: <Ips form={form} content={parseContentByType(content, type)} />,
    CACHE: <Cache form={form} content={parseContentByType(content, type)} />,
    CACHE_CLEAR: <CacheClear form={form} content={parseContentByType(content, type)} />,
    CORS: <Cors form={form} content={parseContentByType(content, type)} />,
    CUSTOM: <Default form={form} content={parseContentByType(content, type)}/>,
    LOG_MASKER: <LogMasker form={form} content={parseContentByType(content, type)}/>,
    OAUTH: <OAuth form={form} content={parseContentByType(content, type)}/>,
    IDENTIFIER: <Identifier form={form} content={parseContentByType(content, type)}/>,
    MIDDLEWARE: <Default form={form} content={parseContentByType(content, type)}/>,
})

const parseContentByType = (content, type) => {

    if (!content) {
        return content
    }

    switch (type) {
        case 'MIDDLEWARE':
        case 'CUSTOM':
            return content
        default:
            return JSON.parse(content)
    }
}

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