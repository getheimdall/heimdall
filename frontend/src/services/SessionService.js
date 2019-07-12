import {JwtUtils} from "../utils/JwtUtils"
import {openModalSession, updateTime} from "../actions/session"

class SessionService {

    _dispatchs;
    _timeoutIds;

    constructor() {
        this._dispatchs = []
        this._timeoutIds = []

        if (!SessionService.instance) {
            SessionService.instance = this
        }

        return SessionService.instance
    }

    addDispatch = (name, dispatch) => {
        this._dispatchs.push({ name: name, dispatch: dispatch })
    }

    setTimeSessionFromToken = token => {
        const timeToExpire = parseInt(JwtUtils.getTimeToExpiresInSeconds(token), 10)
        Session.dispatch('modalSession', updateTime(timeToExpire))
    }

    createTimeOut = time => {
        this.cancelTimeouts()
        const id = setTimeout(() => {
            this.dispatch('modalSession', openModalSession())
        }, time)
        this._timeoutIds.push(id)
    }

    cancelTimeouts = () => {
        if (this._timeoutIds.length) {
            this._timeoutIds.forEach(i => {
                clearTimeout(i)
            })

            while(this._timeoutIds.length) {
                this._timeoutIds.pop()
            }
        }
    }

    dispatch = (name, action) => {
        const dispatch = this._dispatchs.find(d => d.name === name).dispatch
        if (dispatch) {
            dispatch(action)
        }
    }
}

const Session = new SessionService()
Object.freeze(Session)

export default Session