//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'
//components
import { Row, Form, Input, Col, Card, notification } from 'antd'
//actions
import { getAllApps, initLoading, remove } from '../actions/apps'

import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import ListApps from '../components/apps/ListApps'
import PageHeader from '../components/ui/PageHeader'
import FloatButton from '../components/ui/FloatButton'

class Apps extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllApps())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleDelete = (appId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(appId, { offset: this.state.page, limit: this.state.pageSize }))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllApps({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllApps({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { apps, loading, history } = this.props

        if (!apps) return <Loading />

        return (
            <div>
                <PageHeader title={i18n.t('apps')} icon="appstore" />
                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24} type="flex" justify="start">
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('name')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('name')} />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('description')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('description')} />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('clientId')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('client_id')} />)}
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListApps dataSource={apps} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />

                    <FloatButton idButton="addApp" history={history} to="/apps/new" label={i18n.t('add_new_app')} />
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        apps: state.apps.apps,
        loading: state.apps.loading,
        notification: state.apps.notification
    }
}

const AppsWrapped = Form.create({})(Apps)

export default connect(mapStateToProps)(AppsWrapped)