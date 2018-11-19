import {PrivilegesConstants} from "../constants/actions-types"

export default (state={}, action) => {
    switch (action.type) {
        case PrivilegesConstants.GET_PRIVILEGES:
            return { ...state, privileges: action.privileges }
        case PrivilegesConstants.CLEAR_PRIVILEGES:
            const { privileges, ...stateWithoutRoles } = state
            return { ...stateWithoutRoles }
        case PrivilegesConstants.GET_PRIVILEGE:
            return { ...state, role: action.privilege }
        case PrivilegesConstants.CLEAR_PRIVILEGE:
            const { privilege, ...stateWithoutRole } = state
            return { ...stateWithoutRole }
        case PrivilegesConstants.PRIVILEGE_LOADING:
            return { ...state, loading: true }
        case PrivilegesConstants.PRIVILEGE_LOADING_FINISH:
            return { ...state, loading: false }
        case PrivilegesConstants.PRIVILEGE_NOTIFICATION:
            return { ...state, notification: action.notification }
        default:
            return state
    }
}