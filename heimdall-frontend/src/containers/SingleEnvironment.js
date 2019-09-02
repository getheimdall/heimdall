import { Row, Card } from 'antd'
import { connect } from 'react-redux'
import React, { Component } from 'react'
import {notification} from "antd/lib/index"
import { push } from 'connected-react-router'

import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import EnvironmentForm from '../components/environments/EnvironmentForm'
import { getEnvironment, clearEnvironment, save, update, remove, clearEnvironments, initLoading } from '../actions/environments'

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

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
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
        this.props.dispatch(remove(environmentId))
        this.props.dispatch(clearEnvironments())
        this.props.dispatch(push('/environments'))
    }

    render() {
        const { environment } = this.props

        if (this.state.loadUser && !environment) return <Loading />
        const title = environment ? i18n.t('edit') : i18n.t('add')

        return (
            <div>
                <PageHeader title={i18n.t('environments')} icon="codepen" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' ' + i18n.t('environment')}>
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
        loading: state.environments.loading,
        notification: state.environments.notification
    }
}

export default connect(mapStateToProps)(SingleEnvironment)