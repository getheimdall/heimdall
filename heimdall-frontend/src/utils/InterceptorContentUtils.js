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
        }

        return JSON.stringify(content)
    }

    return content
}

const defaultContent = content => {
    return content
}


export const InterceptorContent = {
    cacheContent,
    ipsContent,
    logMaskerContent,
    defaultContent
}