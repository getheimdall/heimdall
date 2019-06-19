import { ResourceConstants } from '../../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case ResourceConstants.ALL_RESOURCES:
            return { ...state, resources: action.resources }
        case ResourceConstants.UPDATE_RESOURCES_WITH_OPERATIONS:
            return { ...state, resources: action.resources }
        case ResourceConstants.CLEAR_RESOURCES:
            const { resources, ...stateWithoutRes } = state
            return { ...stateWithoutRes }
        case ResourceConstants.VISIBLE_MODAL:
            return { ...state, visibleModal: action.visible }
        case ResourceConstants.RESET_RESOURCE:
            return { ...state, resource: {} }
        case ResourceConstants.GET_RESOURCE:
            return { ...state, resource: action.resource }
        case ResourceConstants.INIT_LOADING:
            return { ...state, loading: true }
        case ResourceConstants.FINISH_LOADING:
            return { ...state, loading: false }
        case ResourceConstants.RESOURCE_NOTIFICATION:
            return { ...state, notification: action.notification}
        default:
            return state
    }
}