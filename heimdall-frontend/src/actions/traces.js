import { UserConstants } from '../constants/actions-types'
import { userService } from '../services'
import { push } from 'connected-react-router';

export const initLoading = () => dispatch => {
    dispatch({ type: UserConstants.USER_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: UserConstants.USER_LOADING_FINISH })
}

export const getAllTraces = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = { params: query }
    userService.getUsers(parameters)
        .then(data => {
            dispatch({ type: UserConstants.GET_USERS, traces: data })
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
    dispatch({type: UserConstants.USER_NOTIFICATION, notification})
}
