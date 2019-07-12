import {MiddlewaresConstants} from "../../constants/actions-types";

export default (state = {}, action) => {
    switch (action.type) {
        case MiddlewaresConstants.GET_MIDDLEWARE:
            return {...state, middleware: action.middleware}
        case MiddlewaresConstants.GET_MIDDLEWARES:
            return {...state, middlewares: action.middlewares}
        case MiddlewaresConstants.MIDDLEWARE_LOADING:
            return {...state, loading: true}
        case MiddlewaresConstants.MIDDLEWARE_LOADING_FINISH:
            return {...state, loading: false}
        case MiddlewaresConstants.MIDDLEWARE_NOTIFICATION:
            return {...state, notification: action.notification}
        default:
            return state
    }
}