import { NavbarConstants } from '../constants/actions-types'

const activeRoutingMiddleWare = store => next => action => {
    console.log("Middleware triggered:", action);
    if (action.type === "@@router/LOCATION_CHANGE") {
        const pathname = action.payload.location.pathname

        if (pathname === "/") {
            store.dispatch({ type: NavbarConstants.UPDATE_MENU_KEYS, keys: ['apis'] })
        } else {
            const key = pathname.split('/')
            store.dispatch({ type: NavbarConstants.UPDATE_MENU_KEYS, keys: [key[1]] })
        }
    }
    return next(action);
}

export default activeRoutingMiddleWare