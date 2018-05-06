import { InterceptorConstants } from '../../constants/actions-types'
import { QueueConstants } from '../../constants/actions-types'
import { interceptorService } from '../../services'
import { push } from 'connected-react-router';

export const initLoading = () => dispatch => {
    dispatch({ type: InterceptorConstants.INTERCEPTOR_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: InterceptorConstants.INTERCEPTOR_LOADING_FINISH })
}

export const getAllInterceptors = (query = { offset: 0, limit: 10 }) => dispatch => {
    const parameters = { params: query }
    interceptorService.getInterceptors(parameters)
        .then(data => {
            dispatch({ type: InterceptorConstants.GET_INTERCEPTORS, interceptors: data })
            dispatch(finishLoading())
        })
}

export const clearInterceptors = () => dispatch => {
    dispatch({ type: InterceptorConstants.CLEAR_INTERCEPTORS })
}

export const getAllInterceptorsTypes = () => dispatch => {
    interceptorService.getInterceptorTypes()
        .then(data => {
            dispatch({ type: InterceptorConstants.GET_INTERCEPTORS_TYPES, interceptorTypes: data })
            dispatch(finishLoading())
        })
}

export const clearInterceptorsTypes = () => dispatch => {
    dispatch({ type: InterceptorConstants.CLEAR_INTERCEPTORS_TYPES })
}


export const getInterceptor = interceptorId => dispatch => {
    interceptorService.getInterceptor(interceptorId)
        .then(data => dispatch({ type: InterceptorConstants.GET_INTERCEPTOR, interceptor: data }))
        .catch(error => console.log(error))
}

export const clearInterceptor = () => dispatch => {
    dispatch({ type: InterceptorConstants.CLEAR_INTERCEPTOR })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: InterceptorConstants.INTERCEPTOR_NOTIFICATION, notification })
}

export const processQueue = () => dispatch => {
    dispatch({ type: QueueConstants.PROCESS_QUEUE })
}

export const errorQueue = () => dispatch => {
    dispatch({ type: QueueConstants.PROCESS_QUEUE })
}

export const save = interceptor => dispatch => {
    interceptorService.save(interceptor)
        .then(data => {
            dispatch(sendNotification({ type: 'success', message: 'Interceptor saved' }))
            dispatch(push('/interceptors'))
            dispatch(finishLoading())
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const saveAll = interceptors => dispatch => {
    interceptors.forEach(interceptor => {
        interceptorService.save(interceptor)
            .then(data => dispatch(processQueue()))
            .catch(error => {
                dispatch(errorQueue())
                if (error.response && error.response.status === 400) {
                    dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
                }
            })
    })
}

export const update = interceptor => dispatch => {
    interceptorService.update(interceptor)
        .then(data => {
            dispatch(getInterceptor(interceptor.id))
            dispatch(sendNotification({ type: 'success', message: 'Interceptor updated' }))
            dispatch(finishLoading())
        })
        .catch(error => {
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const updateAll = interceptors => dispatch => {
    interceptors.forEach(interceptor => {
        interceptorService.update(interceptor)
            .then(data => dispatch(processQueue()))
            .catch(error => {
                dispatch(errorQueue())
                if (error.response && error.response.status === 400) {
                    dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
                }
            })
    })
}

export const remove = interceptorId => dispatch => {
    interceptorService.remove(interceptorId)
        .then(data => {
            dispatch(getAllInterceptors())
            dispatch(sendNotification({ type: 'success', message: 'Interceptor removed' }))
        })
}

export const removeAll = interceptors => dispatch => {
    interceptors.forEach(interceptor => {
        interceptorService.remove(interceptor.id)
            .then(data => dispatch(processQueue()))
            .catch(error => {
                dispatch(errorQueue())
                if (error.response && error.response.status === 400) {
                    dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
                }
            })
    })
}
