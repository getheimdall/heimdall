import React, {PureComponent} from 'react'
import {connect} from 'react-redux'
import {userService} from '../../services'
import ComponentAuthority from "../policy/ComponentAuthority";
// Authorization HOC
const Authorization = (privilegesAllowed) => (WrappedComponent) => {
    class WithAuthorization extends PureComponent {

        componentWillMount() {
            if (!userService.isUserLogged()) {
                this.props.history.push('/login')
                localStorage.clear();
            }
        }

        render() {
            return (
                <ComponentAuthority privilegesAllowed={privilegesAllowed}>
                    <WrappedComponent {...this.props} />
                </ComponentAuthority>
            )
        }

    }

    const mapStateToProps = (state) => {
        return {
            loggedIn: state.auth.loggedIn
            //definir roles no props
        }
    }

    return connect(mapStateToProps)(WithAuthorization)
}


export default Authorization;