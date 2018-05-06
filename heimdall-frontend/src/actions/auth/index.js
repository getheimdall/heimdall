import { LOGIN_FAILED, LOGOUT, GET_USER_LOGIN, LOGIN_SUCCESSFUL } from '../../constants/actions-types'
import { userService } from '../../services'
import { notification } from 'antd'
import { push } from 'connected-react-router';

const loginFailed = message => ({
    type: LOGIN_FAILED,
    message
})

const getUserLogin = user => ({
    type: GET_USER_LOGIN,
    user
})

const loginSuccessful = user => ({
    type: LOGIN_SUCCESSFUL,
    user
})

export const login = (login, password) => dispatch => {
    userService.login(login, password)
    .then(data => {
        notification['success']({ message: 'Welcome to Heimdall' })
        dispatch(loginSuccessful(data))
        dispatch(push('/'))
    }).catch(error => {
        notification['error']({ message: 'Login or password incorrect' })
        dispatch(loginFailed('Login or password incorrect'))
        dispatch(push('/login'))
    })
}

export const logout = () => dispatch => {
    dispatch({type: LOGOUT})
    userService.logout()
    dispatch(push('/login'))
}

export const getUser = () => dispatch => {
    let user = userService.getUserLocal()
    dispatch(getUserLogin(user))
}