import { push } from 'connected-react-router'
import i18n from '../../i18n/i18n'
import { AccessTokenConstants } from '../../constants/actions-types'
import { accessTokenService } from '../../services'

export const initLoading = () => dispatch => {
    dispatch({ type: AccessTokenConstants.ACCESS_TOKEN_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: AccessTokenConstants.ACCESS_TOKEN_LOADING_FINISH })
}

export const getAllAccessTokens = (query = {offset: 0, limit: 10}) => dispatch => {
    const parameters = { params: query }
    accessTokenService.getAccessTokens(parameters)
        .then(data => {
            dispatch({ type: AccessTokenConstants.GET_ACCESS_TOKENS, accessTokens: data })
            dispatch(finishLoading())
        })
}

export const clearAccessTokens = () => dispatch => {
    dispatch({ type: AccessTokenConstants.CLEAR_ACCESS_TOKENS })
}

export const getAccessToken = accessTokenId => dispatch => {
    accessTokenService.getAccessToken(accessTokenId)
        .then(data => dispatch({ type: AccessTokenConstants.GET_ACCESS_TOKEN, accessToken: data }))
        .catch(error => console.log(error))
}

export const clearAccessToken = () => dispatch => {
    dispatch({ type: AccessTokenConstants.CLEAR_ACCESS_TOKEN })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: AccessTokenConstants.ACCESS_TOKEN_NOTIFICATION, notification })
}

export const save = accessToken => dispatch => {
    accessTokenService.save(accessToken)
        .then(data => {
            dispatch(sendNotification({ type: 'success', message: i18n.t('access_token_saved') }))
            dispatch(push('/tokens'))
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

export const update = accessToken => dispatch => {
    accessTokenService.update(accessToken)
        .then(data => {
            dispatch(getAccessToken(accessToken.id))
            dispatch(sendNotification({ type: 'success', message: i18n.t('access_token_updated') }))
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

export const remove = (accessTokenId, offset) => dispatch => {
    accessTokenService.remove(accessTokenId)
        .then(data => {
            dispatch(getAllAccessTokens({offset: offset, limit: 10}))
            dispatch(sendNotification({ type: 'success', message: i18n.t('access_token_removed') }))
        })
        .catch(error => {
            console.log(error)
            if (error.response && error.response.status === 400) {
                dispatch(sendNotification({ type: 'error', message: 'Error', description: error.response.data.message }))
            }
            dispatch(finishLoading())
        })
}

