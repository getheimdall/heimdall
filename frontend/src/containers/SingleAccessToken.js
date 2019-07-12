//3rd's
import React, { Component } from 'react'
import { push } from 'connected-react-router'
import { connect } from 'react-redux'
import moment from 'moment'
//components
import { Card, Row, notification } from 'antd'
// actions
import i18n from "../i18n/i18n"
import { isEmpty } from '../utils/CommonUtils'
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import { getAllPlans, clearPlans } from '../actions/plans'
import AccessTokenForm from '../components/access-tokens/AccessTokenForm'
import { appSource, getAppSourceByName, clearAppSource, fetchingApp } from '../actions/apps'
import { getAccessToken, initLoading, clearAccessToken, clearAccessTokens, update, save, remove } from '../actions/access-tokens'


class SingleAccessToken extends Component {

    state = { loadEntity: false, timer: Date.now(), intervalSeconds: 2 }

    componentDidMount() {
        let idAccessToken = this.props.match.params.id
        if (idAccessToken) {
            this.props.dispatch(getAccessToken(idAccessToken))
            this.setState({ ...this.state, loadEntity: true })
        }
        this.props.dispatch(getAllPlans({offset: 0, limit: 50}))
    }

    componentWillReceiveProps(newProps) {
        if (newProps.accessToken && newProps.accessToken !== this.props.accessToken) {
            this.props.dispatch(appSource([newProps.accessToken.app]))
        }

        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearAccessToken())
        this.props.dispatch(clearAppSource())
        this.props.dispatch(clearPlans())
    }

    handleSearch = (searchObject) => {
        if (searchObject && !isEmpty(searchObject) && searchObject.length > 3) {
            var duration = moment.duration(moment().diff(this.state.timer));
            var seconds = duration.asSeconds();
            if (seconds > this.state.intervalSeconds) {
                this.setState({ ...this.state, timer: Date.now() })
                this.props.dispatch(clearAppSource())
                this.props.dispatch(fetchingApp())
                this.props.dispatch(getAppSourceByName(searchObject))
            }
        }
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        if (formObject.id) {
            this.props.dispatch(clearAccessToken())
            this.props.dispatch(update(formObject))
        } else {
            this.props.dispatch(save(formObject))
        }
    }

    handleDelete = (accessTokenId) => {
        this.props.dispatch(remove(accessTokenId))
        this.props.dispatch(clearAccessTokens())
        this.props.dispatch(push('/tokens'))
    }

    render() {
        const { accessToken } = this.props

        if (this.state.loadEntity && !accessToken) return <Loading />
        const title = accessToken ? i18n.t('edit') : i18n.t('add')
        const { appSource } = this.props

        return (
            <div>
                <PageHeader title={i18n.t('access_tokens')} icon="key" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' ' + i18n.t('access_token')}>
                        <AccessTokenForm accessToken={accessToken}
                            plans={this.props.plans}
                            handleDelete={this.handleDelete}
                            handleSubmit={this.handleSubmit}
                            handleSearch={this.handleSearch}
                            loading={this.props.loading}
                            appSource={appSource}
                            fetching={this.props.fetching} />
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        accessToken: state.accessTokens.accessToken,
        loading: state.accessTokens.loading,
        appSource: state.apps.appSource,
        fetching: state.apps.fetching,
        plans: state.plans.plans,
        notification: state.accessTokens.notification
    }
}

export default connect(mapStateToProps)(SingleAccessToken)