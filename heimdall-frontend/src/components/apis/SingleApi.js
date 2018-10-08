import React, { Component } from 'react'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'
import { Card, Row, Tabs, Button, Icon } from 'antd'

import { getApiById, updateApi, deleteApi, resetApiAction } from '../../actions/apis'
import { getAllEnvironments, clearEnvironments } from '../../actions/environments'
import PageHeader from '../ui/PageHeader' // best way?
import i18n from "../../i18n/i18n"
import ApiDefinition from './ApiDefinition'
import Resources from '../../containers/Resources'
import Interceptors from '../../containers/Interceptors'
import Middlewares from '../../containers/Middlewares'
import Loading from '../ui/Loading'

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
        if (!this.props.api || !this.props.environments) return <Loading />

        const { api } = this.props

        return (
            <div className="joy">
                <PageHeader title={i18n.t('apis')} icon="api" />
                <Row>
                    <Card style={{ width: '100%' }} title={api.name}>
                        <Tabs defaultActiveKey="1" className="resource-tour">
                            <TabPane tab={i18n.t('definitions')} key="1">
                                <ApiDefinition api={api} environments={this.props.environments} history={this.props.history} submit={this.props.updateApi} deleteApi={this.props.deleteApi} />
                            </TabPane>
                            <TabPane tab={<div role="tab" className="ant-tabs-tab resource">{i18n.t('resources')}</div>} key="2" >
                                <Resources api={api} />
                            </TabPane>
                            <TabPane tab={<div role="tab" className="ant-tabs-tab interceptors">{i18n.t('interceptors')}</div>} key="3">
                                <Interceptors api={api} />
                            </TabPane>
                            <TabPane tab={<div role="tab" className="ant-tabs-tab middlewares">{i18n.t('middlewares')}</div>} key="4">
                                <Middlewares api={api} />
                            </TabPane>
                        </Tabs>
                    </Card>
                </Row>
                <Row className="h-row">
                    <Button type="primary" onClick={() => this.props.history.push('/apis')} >
                        <Icon type="left" /> {i18n.t('back_to_apis')}
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