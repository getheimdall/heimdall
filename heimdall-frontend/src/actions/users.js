import { push } from 'connected-react-router'

import i18n from "../i18n/i18n"
import { UserConstants } from '../constants/actions-types'
import { userService } from '../services'

export const initLoading = () => dispatch => {
    dispatch({ type: UserConstants.USER_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: UserConstants.USER_LOADING_FINISH })
}

export const getAllUsers = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = { params: query }
    userService.getUsers(parameters)
        .then(data => {
            dispatch({ type: UserConstants.GET_USERS, users: data })
            dispatch(finishLoading())
        })
}

export const clearUsers = () => dispatch => {
    dispatch({ type: UserConstants.CLEAR_USERS })
}

export const getUser = userId => dispatch => {
    userService.getUser(userId)
        .then(data => dispatch({ type: UserConstants.GET_USER, user: data }))
        .catch(error => console.log(error))
}

export const clearUser = () => dispatch => {
    dispatch({ type: UserConstants.CLEAR_USER })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: UserConstants.USER_NOTIFICATION, notification })
}

export const save = user => dispatch => {
    userService.save(user)
        .then(data => {
            dispatch(sendNotification({ type: 'success', message: i18n.t('user_saved') }))
            dispatch(push('/users'))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const update = user => dispatch => {
    userService.update(user)
        .then(data => {
            dispatch(getUser(user.id))
            dispatch(sendNotification({ type: 'success', message: i18n.t('user_updated') }))
            dispatch(push('/users'))
            dispatch(finishLoading())
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: i18n.t('error'), description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

export const remove = userId => dispatch => {
    userService.remove(userId)
        .then(data => {
            dispatch(getAllUsers())
            dispatch(sendNotification({ type: 'success', message: i18n.t('user_removed') }))
        })
}

