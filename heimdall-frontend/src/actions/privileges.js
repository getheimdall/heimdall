import { PrivilegesConstants } from '../constants/actions-types'
import { privilegeService } from '../services'

export const initLoading = () => dispatch => {
    dispatch({ type: PrivilegesConstants.PRIVILEGE_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: PrivilegesConstants.PRIVILEGE_LOADING_FINISH })
}

export const getAllPrivileges = () => dispatch => {
    privilegeService.getAllPrivileges()
        .then(data => {
            dispatch({ type: PrivilegesConstants.GET_PRIVILEGES, privileges: data })
            dispatch(finishLoading())
        })
        .catch(error => {
            dispatch(sendNotification({type: 'error', message: `${error.message}`}))
            dispatch(finishLoading())
        })
}

export const clearPrivileges = () => dispatch => {
    dispatch({ type: PrivilegesConstants.CLEAR_PRIVILEGES })
}

export const clearPrivilege = () => dispatch => {
    dispatch({ type: PrivilegesConstants.CLEAR_PRIVILEGE })
}

export const getPrivilege = privilegeId => dispatch => {
    privilegeService.getPrivilege(privilegeId)
        .then(data => {
            dispatch({ type: PrivilegesConstants.GET_PRIVILEGE, privilege: data })
            dispatch(finishLoading())
        })
        .catch(error => console.log(error))
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: PrivilegesConstants.PRIVILEGE_NOTIFICATION, notification })
}

