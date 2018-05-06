import React, { PureComponent } from 'react'
import { connect } from 'react-redux'
import { userService } from '../../services'
// Authorization HOC
const Authorization = (allowedRoles) => (WrappedComponent) => {
	class WithAuthorization extends PureComponent {
		// eslint-disable-next-line
		constructor(props) {
			super(props)

			// this.state = {
			// 	user: {
			// 		name: 'vcarl',
			// 		role: 'admin'
			// 	}
			// }

		}
		
		componentWillMount() {
			if (!userService.isUserLogged()) {
				this.props.history.push('/login')
			}
		}

		render() {
			// const { role } = this.state.user
			const role = ''
			if (allowedRoles && !allowedRoles.includes(role)) {
				return <h1>No page for you!</h1>
			} else if (userService.isUserLogged()) {
				return <WrappedComponent {...this.props} />
			} else {
				return null
			}
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