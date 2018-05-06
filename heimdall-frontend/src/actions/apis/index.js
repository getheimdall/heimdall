import { ApiConstants } from '../../constants/actions-types'
import { notification } from 'antd'

import { apiService } from '../../services'
import { push } from 'connected-react-router';


const receiveApis = apis => ({
    type: ApiConstants.RECEIVE_APIS,
    apis
})

// Get async apis list
export const getAllApis = () => dispatch => {
    apiService.getApis()
        .then(data => dispatch(receiveApis(data)))
        .catch(error => console.log('Error: ', error))
}

const receiveApi = api => ({ type: ApiConstants.RECEIVE_API, api })

export const getApiById = (id) => dispatch => {
    apiService.getApiById(id)
        .then(data => dispatch(receiveApi(data)))
        .catch(error => {
            notification['error']({ message: error.message })
            console.log(error.message)
            dispatch(push('/apis'))
        })
}

const updateApiAction = api => ({ type: ApiConstants.UPDATE_API, api })
export const resetApiAction = () => ({ type: ApiConstants.RESET_API })

export const updateApi = (api) => dispatch => {
    dispatch(resetApiAction())
    apiService.updateApi(api)
        .then(data => {
            notification['success']({ message: 'Update Successful' })
            dispatch(updateApiAction(data))
            // dispatch(push('/apis/' + api.id))
        })
        .catch(error => console.log(error))
}

export const getNewApi = () => dispatch => {
    dispatch({ type: ApiConstants.NEW_API })
}

const saveApiAction = api => ({ type: ApiConstants.SAVE_API, api })

export const saveApi = api => dispatch => {
    apiService.saveApi(api)
        .then(data => {
            notification['success']({ message: 'Save Successful' })
            dispatch(saveApiAction(data))
            dispatch(push('/apis'))
        })
        .catch(error => console.log(error))
}

const deleteApiAction = id => ({
    type: ApiConstants.SAVE_API,
})

export const deleteApi = id => dispatch => {
    apiService.deleteApi(id)
        .then(data => {
            notification['success']({ message: 'Api Deleted' })
            dispatch(deleteApiAction(data))
            dispatch(push('/apis'))
        })
        .catch(error => console.log(error))
}

export const apiSource = apiSource => dispatch => {
    dispatch({ type: ApiConstants.API_SOURCE, apiSource })
}

export const clearApiSource = () => dispatch => {
    dispatch({ type: ApiConstants.CLEAR_API_SOURCE })
}

export const fetchingApi = () => dispatch => {
    dispatch({ type: ApiConstants.FETCHING_API_SOURCE })
}

export const finishFetchingApi = () => dispatch => {
    dispatch({ type: ApiConstants.FINISH_FETCHING_API_SOURCE })
}

export const getApiSourceByName = name => dispatch => {
    const parameters = { params: { name: name } }
    apiService.getApis(parameters)
        .then(data => {
            dispatch(apiSource(data))
            dispatch(finishFetchingApi())
        })
}