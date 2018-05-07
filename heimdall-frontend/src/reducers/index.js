import { combineReducers } from 'redux'
// Reducers
import apis from './apis'
import auth from './auth'
import environments from './environments'
import resources from './resources'
import operations from './operations'
import developers from './developers'
import apps from './apps'
import plans from './plans'
import accessTokens from './access-tokens'
import interceptors from './interceptors'
import queue from './Queue'
import users from './Users'
import roles from './Roles'
import caches from './Caches'

export default combineReducers({
    apis,
    auth,
    environments,
    resources,
    operations,
    developers,
    apps,
    plans,
    accessTokens,
    interceptors,
    queue,
    users,
    roles,
    caches
})