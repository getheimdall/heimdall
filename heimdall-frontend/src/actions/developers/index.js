import { push } from 'connected-react-router'

import i18n from "../../i18n/i18n"
import { DeveloperConstants } from '../../constants/actions-types'
import { developerService } from '../../services'

export const initLoading = () => dispatch => {
    dispatch({ type: DeveloperConstants.DEVELOPER_LOADING })
}

export const finishLoading = () => dispatch => {
    dispatch({ type: DeveloperConstants.DEVELOPER_LOADING_FINISH })
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: DeveloperConstants.DEVELOPER_NOTIFICATION, notification })
}

export const getAllDevelopers = (query = {offset: 0, limit: 10} ) => dispatch => {
    const parameters = { params: query }
    developerService.getDevelopers(parameters)
        .then(data => {
            dispatch({ type: DeveloperConstants.GET_DEVELOPERS, developers: data })
            dispatch(finishLoading())
        })
}

export const clearDevelopers = () => dispatch => {
    dispatch({ type: DeveloperConstants.CLEAR_DEVELOPERS })
}

export const getDeveloper = developerId => dispatch => {
    developerService.getDeveloper(developerId)
        .then(data => dispatch({ type: DeveloperConstants.GET_DEVELOPER, developer: data }))
        .catch(error => console.log(error))
}

export const clearDeveloper = () => dispatch => {
    dispatch({ type: DeveloperConstants.CLEAR_DEVELOPER })
}

export const save = developer => dispatch => {
    developerService.save(developer)
        .then(data => {
            dispatch(getAllDevelopers())
            dispatch(sendNotification({ type: 'success', message: i18n.t('developer_saved') }))
            dispatch(push('/developers'))
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

export const update = developer => dispatch => {
    developerService.update(developer)
        .then(data => {
            dispatch(getDeveloper(developer.id))
            dispatch(sendNotification({ type: 'success', message: i18n.t('developer_updated') }))
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

export const remove = (developerId, query) => dispatch => {
    developerService.remove(developerId)
        .then(data => {
            dispatch(getAllDevelopers(query))
            dispatch(sendNotification({ type: 'success', message: i18n.t('developer_removed') }))
        })
}

export const developerSource = developerSource => dispatch => {
    dispatch({ type: DeveloperConstants.DEVELOPER_SOURCE, developerSource })
}

export const clearDeveloperSource = () => dispatch => {
    dispatch({ type: DeveloperConstants.CLEAR_DEVELOPER_SOURCE })
}

export const fetchingDeveloper = () => dispatch => {
    dispatch({ type: DeveloperConstants.FETCHING_DEVELOPER_SOURCE })
}

export const finishFetchingDeveloper = () => dispatch => {
    dispatch({ type: DeveloperConstants.FINISH_FETCHING_DEVELOPER_SOURCE })
}

export const getDeveloperSourceByEmail = email => dispatch => {
    const parameters = { params: { email: email } }
    developerService.getDevelopers(parameters)
        .then(data => {
            dispatch(developerSource(data))
            dispatch(finishFetchingDeveloper())
        })
}