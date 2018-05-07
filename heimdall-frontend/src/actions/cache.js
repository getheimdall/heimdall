import { CacheConstants } from '../constants/actions-types'
import { cacheService } from '../services'

export const initLoading = () => dispatch => {
    dispatch({ type: CacheConstants.CACHE_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: CacheConstants.CACHE_LOADING_FINISH })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: CacheConstants.CACHE_NOTIFICATION, notification })
}

export const clearCaches = () => dispatch => {
    cacheService.clearCaches()
        .then(data => {
            dispatch(sendNotification({ type: 'success', message: 'Cache cleared' }))
            dispatch(finishLoading())
        })
}