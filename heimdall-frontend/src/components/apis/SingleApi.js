import React, {Component, Suspense, lazy} from 'react'
import {connect} from 'react-redux'
import {bindActionCreators} from 'redux'
import {Button, Card, Icon, Row, Tabs} from 'antd'

import i18n from "../../i18n/i18n"
import Loading from '../ui/Loading'
import PageHeader from '../ui/PageHeader' // best way?
import ApiDefinition from './ApiDefinition'

import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import { privileges } from '../../constants/privileges-types'
import {clearEnvironments, getAllEnvironments} from '../../actions/environments'
import {deleteApi, getApiById, resetApiAction, updateApi} from '../../actions/apis'

const Resources = lazy(() => import('../../containers/Resources'))
const Middlewares = lazy(() => import('../../containers/Middlewares'))
const Interceptors = lazy(() => import('../../containers/Interceptors'))
const Scopes = lazy(() => import('../../containers/Scopes'))

const TabPane = Tabs.TabPane;

class SingleApi extends Component {

    componentDidMount() {
        let idApi = this.props.match.params.id
        if (idApi) {
            this.props.getApiById(idApi)
            this.props.getAllEnvironments()
        }
    }

    componentWillUnmount() {
        this.props.clearEnvironments()
        this.props.resetApiAction()
    }

    // modal methods
    showModal = () => {
        this.setState({
            visible: true,
        })
    }

    handleCancel = (e) => {
        this.setState({
            visible: false,
        })
    }

    render() {
        if (!this.props.api || !this.props.environments) return <Loading/>

        const {api} = this.props

        return (
            <div className="joy">
                <PageHeader title={i18n.t('apis')} icon="api" />
                <Row>
                    <Card style={{width: '100%'}} title={api.name}>
                        <Tabs defaultActiveKey="1" className="resource-tour">
                            <TabPane tab={i18n.t('definitions')} key="1">
                                <ApiDefinition api={api} environments={this.props.environments} history={this.props.history} submit={this.props.updateApi} deleteApi={this.props.deleteApi} />
                            </TabPane>
                            {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_RESOURCE]) &&
                            <TabPane tab={<div role="tab" className="ant-tabs-tab resource">{i18n.t('resources')}</div>}
                                     key="2">
                                <Suspense fallback={<Loading/>}>
                                    <Resources api={api}/>
                                </Suspense>
                            </TabPane>}
                            {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_INTERCEPTOR]) &&
                            <TabPane tab={<div role="tab" className="ant-tabs-tab interceptors">{i18n.t('interceptors')}</div>}
                                     key="3">
                                <Suspense fallback={<Loading/>}>
                                    <Interceptors api={api}/>
                                </Suspense>
                            </TabPane>}
                            {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_MIDDLEWARE]) &&
                            <TabPane tab={<div role="tab" className="ant-tabs-tab middlewares">{i18n.t('middlewares')}</div>}
                                     key="4">
                                <Suspense fallback={<Loading/>}>
                                    <Middlewares api={api}/>
                                </Suspense>
                            </TabPane>}
                            {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_READ_SCOPE]) &&
                            <TabPane tab={<div role="tab" className="ant-tabs-tab">{i18n.t('scopes')}</div>} key="5">
                                <Suspense fallback={<Loading/>}>
                                    <Scopes api={api}/>
                                </Suspense>
                            </TabPane>}
                        </Tabs>
                    </Card>
                </Row>
                <Row className="h-row">
                    <Button type="primary" onClick={() => this.props.history.push('/apis')}>
                        <Icon type="left"/> {i18n.t('back_to_apis')}
                    </Button>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        api: state.apis.api,
        environments: state.environments.environments
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getApiById: bindActionCreators(getApiById, dispatch),
        updateApi: bindActionCreators(updateApi, dispatch),
        resetApiAction: bindActionCreators(resetApiAction, dispatch),
        getAllEnvironments: bindActionCreators(getAllEnvironments, dispatch),
        clearEnvironments: bindActionCreators(clearEnvironments, dispatch),
        deleteApi: bindActionCreators(deleteApi, dispatch),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(SingleApi);