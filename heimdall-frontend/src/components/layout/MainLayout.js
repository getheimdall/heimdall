import React, { Component } from 'react'
import { Layout, Row, Col } from 'antd'

import NavBar from '../ui/NavBar'
import SideBar from '../ui/SideBar'
import { infoService } from './../../services/InfoService'

class MainLayout extends Component {

	state = { steps: [], keepTour: true, version: '1.9.1' }

	componentDidMount() {
		infoService.getVersion().then(version => {
			if (version) {
				version = version.replace('-SNAPSHOT', '')
				version = version.replace('-RELEASE', '')
				this.setState({ ...this.state, version: version})
			}
		})
	}

	addSteps = (steps) => {
		let newSteps = steps;

		if (!Array.isArray(newSteps)) {
			newSteps = [newSteps];
		}

		if (!newSteps.length) {
			return;
		}

		// Force setState to be synchronous to keep step order.
		this.setState(currentState => {
			currentState.steps = currentState.steps.concat(newSteps);
			return currentState;
		});
	}

	clearSteps = () => {
		this.setState({...this.state, steps: []})
	}

	handleTour = (check) => {
		this.setState({...this.state, keepTour: check})
	}

	render() {
		const { Header, Content, Footer } = Layout
		const { history } = this.props
		const { version } = this.state
		// const version = process.env.REACT_APP_VERSION

		return (
			<Layout className="App">
				<SideBar history={history} collapse={true} />
				<Layout>
					<Header className="h-header">
						<NavBar history={history} />
					</Header>
					<Content>
						<Row>
							<Col sm={24}>
								{React.cloneElement(this.props.children)}
							</Col>
						</Row>
					</Content>
					<Footer>
						<a href="http://www.conductor.com.br">Conductor Tecnologia</a>
						<br/>
						<span>Version: {version}</span>
					</Footer>
				</Layout>
			</Layout>
		)
	}
}

export default MainLayout
