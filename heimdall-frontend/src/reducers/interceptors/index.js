import { InterceptorConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case InterceptorConstants.GET_INTERCEPTORS:
            return { ...state, interceptors: action.interceptors }
        case InterceptorConstants.CLEAR_INTERCEPTORS:
            const { interceptors, ...stateWithoutInts } = state
            return { ...stateWithoutInts }
        case InterceptorConstants.GET_INTERCEPTORS_TYPES:
            return { ...state, interceptorTypes: action.interceptorTypes }
        case InterceptorConstants.CLEAR_INTERCEPTORS_TYPES:
            const { interceptorTypes, ...stateWithoutTypes } = state
            return { ...stateWithoutTypes }
        case InterceptorConstants.GET_INTERCEPTOR:
            return { ...state, interceptor: action.interceptor }
        case InterceptorConstants.CLEAR_INTERCEPTOR:
            const { interceptor, ...stateWithoutInt } = state
            return { ...stateWithoutInt }
        case InterceptorConstants.INTERCEPTOR_LOADING:
            return { ...state, loading: true }
        case InterceptorConstants.INTERCEPTOR_LOADING_FINISH:
            return { ...state, loading: false }
        case InterceptorConstants.INTERCEPTOR_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}