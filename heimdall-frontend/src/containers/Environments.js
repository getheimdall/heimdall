import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Row, notification } from 'antd'

import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import RouteButton from '../components/ui/RouteButton'
import {privileges} from "../constants/privileges-types"
import ComponentAuthority from "../components/policy/ComponentAuthority"
import ListEnvironments from '../components/environments/ListEnvironments'
import { getAllEnvironments, remove, initLoading } from '../actions/environments'

class Environments extends Component {

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllEnvironments())
    }

    handleDelete = (environmentId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(environmentId))
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    render() {
        const { environments, loading, history } = this.props

        if (!environments) return <Loading />

        return (
            <div>
                <PageHeader title={i18n.t('environments')} icon="codepen" />
                <Row className="h-row bg-white">
                    <ListEnvironments environments={environments} handleDelete={this.handleDelete} />
                    {loading && <Loading />}
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_ENVIRONMENT]}>
                        <RouteButton idButton="addEnvironment" history={history} to="/environments/new" label={i18n.t('add_new_environment')} />
                    </ComponentAuthority>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        environments: state.environments.environments,
        notification: state.environments.notification,
        loading: state.environments.loading
    }
}

export default connect(mapStateToProps)(Environments)