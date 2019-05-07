import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { ConnectedRouter } from 'connected-react-router'
import Routes from '../routes'
import ModalSession from "./ModalSession"

class App extends Component {

	render() {
		const { history } = this.props

		return (
			<ConnectedRouter history={history}>
				<div>
                    <Routes history={history} />
                    <ModalSession history={history}/>
				</div>
				{/* {routes} */}
			</ConnectedRouter>
		)
	}
}

App.propTypes = {
	history: PropTypes.object.isRequired
}

export default App
