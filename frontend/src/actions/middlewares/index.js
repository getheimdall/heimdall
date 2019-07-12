import i18n from '../../i18n/i18n'
import {MiddlewaresConstants} from "../../constants/actions-types"
import {middlewareService} from "../../services/MiddlewareService"
import {FileUtils} from "../../utils/FileUtils"

export const initLoading = () => dispatch => {
    dispatch({type: MiddlewaresConstants.MIDDLEWARE_LOADING})
}

export const finishLoading = () => dispatch => {
    dispatch({type: MiddlewaresConstants.MIDDLEWARE_LOADING_FINISH})
}

export const sendNotification = notification => dispatch => {
    dispatch({ type: MiddlewaresConstants.MIDDLEWARE_NOTIFICATION, notification })
}

export const save = (data, apiId, dragger) => dispatch => {
    middlewareService.save(data, apiId)
        .then(response => {
            dispatch(finishLoading())
            dragger.onSuccess();
        })
        .catch(error => {
            dispatch(finishLoading())
            dragger.onError(error);
        })
}

export const getMiddlewares = (query = { offset: 0, limit: 10 }, apiId) => dispatch => {
    const parameters = { params: query }
    middlewareService.getMiddlewares(parameters, apiId)
        .then(data => {
            dispatch({ type: MiddlewaresConstants.GET_MIDDLEWARES, middlewares: data})
            dispatch(finishLoading())
        })
}

export const getMiddleware = (id, apiId) => dispatch => {
    middlewareService.getMiddleware(id, apiId)
        .then(data => {
            dispatch({ type: MiddlewaresConstants.GET_MIDDLEWARE, middleware: data})
            dispatch(finishLoading())
        })
}

export const downloadMiddleware = (id, apiId) => dispatch => {
    middlewareService.downloadMiddleware(id, apiId)
        .then(response => {
            FileUtils.fileDownload(response.data, response.headers['filename'])
            dispatch({ type: MiddlewaresConstants.MIDDLEWARE_DOWNLOAD})
            dispatch(finishLoading())
        })
        .catch(() => {
            dispatch(sendNotification({ type: "error", message: i18n.t('failed_download_file') }))
        })
}
