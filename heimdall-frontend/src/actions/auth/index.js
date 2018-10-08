import { notification } from 'antd'
import { push } from 'connected-react-router';

import i18n from "../../i18n/i18n";
import { AuthConstants } from '../../constants/actions-types'
import { userService } from '../../services'

const loginFailed = message => ({
    type: AuthConstants.LOGIN_FAILED,
    message
})

const getUserLogin = user => ({
    type: AuthConstants.GET_USER_LOGIN,
    user
})

const loginSuccessful = user => ({
    type: AuthConstants.LOGIN_SUCCESSFUL,
    user
})

export const initLoading = () => dispatch => {
    dispatch({ type: AuthConstants.AUTH_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: AuthConstants.AUTH_LOADING_FINISH })
}

export const login = (login, password) => dispatch => {
    dispatch(initLoading())
    userService.login(login, password)
    .then(data => {
        notification['success']({ message: i18n.t('welcome_heimdall') })
        dispatch(loginSuccessful(data))
        dispatch(push('/'))
        dispatch(finishLoading())
    }).catch(error => {
        notification['error']({ message: i18n.t('username_password_incorrect') })
        dispatch(loginFailed(i18n.t('username_password_incorrect')))
        dispatch(push('/login'))
        dispatch(finishLoading())
    })
}

export const logout = () => dispatch => {
    dispatch({type: AuthConstants.LOGOUT})
    userService.logout()
    dispatch(push('/login'))
}

export const getUser = () => dispatch => {
    let user = userService.getUserLocal()
    dispatch(getUserLogin(user))
}