import { notification } from 'antd'
import {push} from 'connected-react-router'

import i18n from "../../i18n/i18n"
import {userService} from '../../services'
import {closeModalSession} from "../session"
import { AuthConstants } from '../../constants/actions-types'

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

export const login = (login, password, renderToHomePage, captchaResponse) => dispatch => {
    dispatch(initLoading())
    userService.login(login, password, captchaResponse)
    .then(data => {
        dispatch(loginSuccessful(data))
        if (renderToHomePage) {
            notification['success']({ message: i18n.t('welcome_heimdall') })
            dispatch(push('/'))
        } else {
            dispatch(closeModalSession())
        }
        dispatch(finishLoading())
    }).catch(error => {
        if(error.response.data.message.includes('captcha')){
            notification['error']({ message: i18n.t('invalid_captcha') })
            dispatch(loginFailed(i18n.t('invalid_captcha')))
        }else{
            notification['error']({ message: i18n.t('username_password_incorrect') })
            dispatch(loginFailed(i18n.t('username_password_incorrect')))
        }
        dispatch(closeModalSession())
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