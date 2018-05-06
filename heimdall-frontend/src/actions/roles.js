import { RoleConstants } from '../constants/actions-types'
import { roleService } from '../services'
import { push } from 'connected-react-router';

export const initLoading = () => dispatch => {
    dispatch({ type: RoleConstants.ROLE_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: RoleConstants.ROLE_LOADING_FINISH })
}

export const getAllRoles = () => dispatch => {
    roleService.getRoles()
        .then(data => {
            dispatch({ type: RoleConstants.GET_ROLES, roles: data })
            dispatch(finishLoading())
        })
}

export const clearRoles = () => dispatch => {
    dispatch({ type: RoleConstants.CLEAR_ROLES })
}

export const getRole = roleId => dispatch => {
    roleService.getRole(roleId)
        .then(data => dispatch({ type: RoleConstants.GET_ROLE, role: data }))
        .catch(error => console.log(error))
}

export const clearRole = () => dispatch => {
    dispatch({ type: RoleConstants.CLEAR_ROLE })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: RoleConstants.ROLE_NOTIFICATION, notification })
}

export const save = role => dispatch => {
    roleService.save(role)
        .then(data => {
            dispatch(sendNotification({ type: 'success', message: 'Role saved' }))
            dispatch(push('/roles'))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const update = role => dispatch => {
    roleService.update(role)
        .then(data => {
            dispatch(getRole(role.id))
            dispatch(sendNotification({ type: 'success', message: 'Role updated' }))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const remove = roleId => dispatch => {
    roleService.remove(roleId)
        .then(data => {
            dispatch(getAllRoles())
            dispatch(sendNotification({ type: 'success', message: 'Role removed' }))
        })
}

