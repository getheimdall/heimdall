const cacheContent = content => {
    if (content) {
        const { headers, queryParams } = content

        if (headers && typeof headers === 'string') {
            const headersSplit = headers.split(',')
            content.headers = headersSplit.map(header => header.trim())
        }

        if (queryParams && typeof queryParams === 'string') {
            const queryParamsSplit = queryParams.split(',')
            content.queryParams = queryParamsSplit.map(queryParam => queryParam.trim())
        }

        if (!headers) {
            delete content.headers
        }

        if (!queryParams){
            delete content.queryParams
        }

        return JSON.stringify(content)
    }

    return content
}

const ipsContent = content => {
    if (content) {
        const { ips } = content

        if (ips && typeof ips === 'string') {
            const ipsSplit = ips.split(',')
            content.ips = ipsSplit.map(ip => ip.trim())
        }

        return JSON.stringify(content)
    }

    return content
}

const logMaskerContent = content => {
    if (content) {

        const { ignoredHeaders } = content

        if (ignoredHeaders && typeof ignoredHeaders === 'string') {
            const ignoredHeadersSplit = ignoredHeaders.split(',')
            content.ignoredHeaders = ignoredHeadersSplit.map(ignoredHeader => ignoredHeader.trim())
        } else {
            content.ignoredHeaders = []
        }

        return JSON.stringify(content)
    }

    return content
}

const corsContent = content => {
    return JSON.stringify(content.cors)
}

const stringifyContent = content => {
    if (content) {
        return JSON.stringify(content)
    }

    return content
}

const simpleContent = content => {
    return content
}

const logWriterContent = content => {
    if (content) {

        const { requiredHeaders } = content

        if (requiredHeaders && typeof requiredHeaders === 'string') {
            const requiredHeadersSplit = requiredHeaders.split(',')
            content.requiredHeaders = requiredHeadersSplit.map(requiredHeader => requiredHeader.trim())
        } else {
            content.requiredHeaders = []
        }

        return JSON.stringify(content)
    }

    return content
}


export const InterceptorContent = {
    cacheContent,
    ipsContent,
    logMaskerContent,
    stringifyContent,
    simpleContent,
    corsContent,
    logWriterContent
}