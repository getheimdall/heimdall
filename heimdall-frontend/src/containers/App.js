import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { ConnectedRouter } from 'connected-react-router'
import Routes from '../routes'

class App extends Component {

	render() {
		const { history } = this.props

		return (
			<ConnectedRouter history={history}>
				<Routes history={history} />
				{/* {routes} */}
			</ConnectedRouter>
		)
	}
}

App.propTypes = {
	history: PropTypes.object.isRequired
}

export default App
