import { RoleConstants } from '../constants/actions-types'

export default (state = {}, action) => {
    switch (action.type) {
        case RoleConstants.GET_ROLES:
            return { ...state, roles: action.roles }
        case RoleConstants.CLEAR_ROLES:
            const { roles, ...stateWithoutRoles } = state
            return { ...stateWithoutRoles }
        case RoleConstants.GET_ROLE:
            return { ...state, role: action.role }
        case RoleConstants.CLEAR_ROLE:
            const { role, ...stateWithoutRole } = state
            return { ...stateWithoutRole }
        case RoleConstants.ROLE_LOADING:
            return { ...state, loading: true }
        case RoleConstants.ROLE_LOADING_FINISH:
            return { ...state, loading: false }
        case RoleConstants.ROLE_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}