import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Row, Card } from 'antd'

import { getEnvironment, clearEnvironment, save, update, remove, clearEnvironments, initLoading } from '../actions/environments';

import PageHeader from '../components/ui/PageHeader'
import Loading from '../components/ui/Loading';
import EnvironmentForm from '../components/environments/EnvironmentForm';
import { push } from 'connected-react-router';

class SingleEnvironment extends Component {

    state = { loadUser: false }

    componentDidMount() {
        let idEnv = this.props.match.params.id
        if (idEnv) {
            this.props.dispatch(getEnvironment(idEnv))
            this.setState({ ...this.state, loadUser: true })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearEnvironment())
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        if (formObject.id) {
            this.props.dispatch(clearEnvironment())
            this.props.dispatch(update(formObject))
        } else {
            this.props.dispatch(save(formObject))
        }
    }

    handleDelete = (environmentId) => {
        console.log(environmentId)
        this.props.dispatch(remove(environmentId))
        this.props.dispatch(clearEnvironments())
        this.props.dispatch(push('/environments'))
    }

    render() {
        const { environment } = this.props

        if (this.state.loadUser && !environment) return <Loading />
        const title = environment ? 'Edit' : 'Add'

        return (
            <div>
                <PageHeader title="Environments" icon="codepen" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' Environment'}>
                        <EnvironmentForm environment={environment} handleDelete={this.handleDelete} handleSubmit={this.handleSubmit} loading={this.props.loading} />
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        environment: state.environments.environment,
        loading: state.environments.loading
    }
}

export default connect(mapStateToProps)(SingleEnvironment)