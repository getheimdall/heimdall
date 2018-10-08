//3rd's
import React, {Component} from 'react'
import {push} from 'connected-react-router'
import {connect} from 'react-redux'
import moment from 'moment'
//components
import {Card, Row} from 'antd'

// actions
import i18n from "../i18n/i18n"
import {isEmpty} from '../utils/CommonUtils'
import Loading from '../components/ui/Loading'
import AppForm from '../components/apps/AppForm'
import PageHeader from '../components/ui/PageHeader'
import {getAllPlans, clearPlans} from '../actions/plans'
import {getApp, initLoading, clearApp, clearApps, update, save, remove} from '../actions/apps'
import {developerSource, getDeveloperSourceByEmail, clearDeveloperSource, fetchingDeveloper} from '../actions/developers'


class SingleApp extends Component {

    state = {loadEntity: false, timer: Date.now(), intervalSeconds: 2}

    componentDidMount() {
        let idApp = this.props.match.params.id
        if (idApp) {
            this.props.dispatch(getApp(idApp))
            this.setState({...this.state, loadEntity: true})
        }
        this.props.dispatch(getAllPlans({offset: 0, limit: 50}))
    }

    componentWillReceiveProps(newProps) {
        if (newProps.app && newProps.app !== this.props.app) {
            this.props.dispatch(developerSource([newProps.app.developer]))
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearApp())
        this.props.dispatch(clearDeveloperSource())
        this.props.dispatch(clearPlans())
    }

    handleSearch = (searchObject) => {
        if (searchObject && !isEmpty(searchObject) && searchObject.length > 3) {
            var duration = moment.duration(moment().diff(this.state.timer));
            var seconds = duration.asSeconds();
            if (seconds > this.state.intervalSeconds) {
                this.setState({...this.state, timer: Date.now()})
                this.props.dispatch(clearDeveloperSource())
                this.props.dispatch(fetchingDeveloper())
                this.props.dispatch(getDeveloperSourceByEmail(searchObject))
            }
        }
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        if (formObject.id) {
            this.props.dispatch(clearApp())
            this.props.dispatch(update(formObject))
        } else {
            this.props.dispatch(save(formObject))
        }
    }

    handleDelete = (appId) => {
        this.props.dispatch(remove(appId))
        this.props.dispatch(clearApps())
        this.props.dispatch(push('/apps'))
    }

    render() {
        const {app} = this.props

        if (this.state.loadEntity && !app) return <Loading/>
        const title = app ? i18n.t('edit') : i18n.t('add')
        const {developerSource} = this.props

        return (
            <div>
                <PageHeader title={i18n.t('apps')} icon="appstore"/>
                <Row className="h-row bg-white">
                    <Card style={{width: '100%'}} title={title + ' ' + i18n.t('app')}>
                        <AppForm app={app}
                                 plans={this.props.plans}
                                 handleDelete={this.handleDelete}
                                 handleSubmit={this.handleSubmit}
                                 handleSearch={this.handleSearch}
                                 loading={this.props.loading}
                                 developerSource={developerSource}
                                 fetching={this.props.fetching}/>
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        app: state.apps.app,
        loading: state.apps.loading,
        developerSource: state.developers.developerSource,
        fetching: state.developers.fetching,
        plans: state.plans.plans
    }
}

export default connect(mapStateToProps)(SingleApp)