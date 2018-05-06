import React, { Component } from 'react'
import { Layout, Row, Col } from 'antd'

import NavBar from '../ui/NavBar'
import SideBar from '../ui/SideBar'
import Joyride from 'react-joyride'

class MainLayout extends Component {

	state = { steps: [], keepTour: true }

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

	handleTour = (check) => {
		this.setState({...this.state, keepTour: check})
	}

	render() {
		const { Header, Content, Footer } = Layout
		const { history } = this.props

		return (
			<Layout className="App">
				<Joyride
					ref={c => (this.joyride = c)}
					steps={this.state.steps}
					type="continuous"
					showSkipButton={true}
					run={this.state.keepTour}
				/>
				<SideBar addSteps={this.addSteps} history={history} collapse={true} />
				<Layout>
					<Header className="h-header">
						<NavBar joyride={this.joyride} handleTour={this.handleTour} history={history} />
					</Header>
					<Content>
						<Row>
							<Col sm={24}>
								{React.cloneElement(this.props.children, { addSteps: this.addSteps })}
							</Col>
						</Row>
					</Content>
					<Footer>
						<a href="http://conductor.com.br">Conductor Tecnologia</a>
					</Footer>
				</Layout>
			</Layout>
		)
	}
}

export default MainLayout
