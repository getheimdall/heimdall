export const countInterceptorsInOperation = (interceptors, operationId) => {
    if (!interceptors){
        return 0
    }
    return interceptors.filter(f => {
        return f.lifeCycle === 'OPERATION' && f.referenceId === operationId
        }
    ).length
}