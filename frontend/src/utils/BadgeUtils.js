export const countInterceptorsByCycle = (interceptors, typeCycle, referenceId) => {
    if (!interceptors){
        return 0
    }
    return interceptors.filter(f => {
        return f.lifeCycle === typeCycle && f.referenceId === referenceId
        }
    ).length
}