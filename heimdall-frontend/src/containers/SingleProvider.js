import React from 'react'
import { connect } from 'react-redux'
import {Card, notification, Row} from "antd"
import {push} from "connected-react-router"

import i18n from "../i18n/i18n"
import PageHeader from "../components/ui/PageHeader"
import ProviderForm from "../components/providers/ProviderForm"
import { clearProvider, clearProviders, getProvider, initLoading, save, update, remove } from "../actions/providers"
import Loading from "../components/ui/Loading"

class SingleProvider extends React.Component {

    state = { loadEntity: false, timer: Date.now(), intervalSeconds: 2 }

    componentDidMount() {
        let providerId = this.props.match.params.id
        if (providerId) {
            this.props.dispatch(initLoading())
            this.props.dispatch(getProvider(providerId))
            this.setState({...this.state, loadEntity: true})
        }
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearProvider())
        this.props.dispatch(clearProviders())
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        if (formObject.id) {
            this.props.dispatch(update(formObject))
        } else {
            this.props.dispatch(save(formObject))
        }
    }

    handleDelete = (providerId) => {
        this.props.dispatch(remove(providerId))
        this.props.dispatch(push('/providers'))
    }

    render() {

        const { provider, loading } = this.props

        if (loading === undefined || loading) return <Loading />

        const title = provider ? i18n.t('edit') : i18n.t('add')

        return (
            <div>
                <PageHeader title={i18n.t('providers')} icon="cluster"/>
                <Row className="h-row bg-white">
                    <Card style={{width: '100%'}} title={`${title} ${i18n.t('provider')}`}>
                        <ProviderForm
                            loading={this.props.loading}
                            provider={provider}
                            handleDelete={this.handleDelete}
                            handleSubmit={this.handleSubmit}
                        />
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        provider: state.providers.provider,
        loading: state.providers.loading,
        notification: state.providers.notification
    }
}

export default connect(mapStateToProps)(SingleProvider)