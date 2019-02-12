import {openModalSession} from "../actions/session";

class SessionManagement {

    _dispatchs;
    _timeoutIds;

    constructor() {
        this._dispatchs = []
        this._timeoutIds = []

        if (!SessionManagement.instance) {
            SessionManagement.instance = this
        }

        return SessionManagement.instance
    }

    addDispatch = (name, dispatch) => {
        this._dispatchs.push({ name: name, dispatch: dispatch })
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

const Session = new SessionManagement()
Object.freeze(Session)

export default Session