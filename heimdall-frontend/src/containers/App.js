import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { ConnectedRouter } from 'connected-react-router'
import Routes from '../routes'
import { withI18n } from 'react-i18next'

class App extends Component {

	state = { lng: ''}

	componentDidMount() {
		const { i18n } = this.props

		if (i18n) {
			this.setState({ ...this.state, lng: i18n.language })
		}
	}

    shouldComponentUpdate(props) {
		console.log('CALL SHOULD COMPONENT UPDATE IN APP!')
    	const { i18n } = props
		return i18n && i18n.language !== this.state.lng
	}

	render() {
		console.log(this.props)
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

export default withI18n()(App)
