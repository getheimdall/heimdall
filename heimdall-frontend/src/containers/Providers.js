import React, {Component} from 'react'
import { connect } from 'react-redux'
import {Card, Col, Form, Row, Input, notification} from 'antd'

import i18n from "../i18n/i18n"
import Loading from "../components/ui/Loading"
import PageHeader from "../components/ui/PageHeader"
import RouteButton from "../components/ui/RouteButton"
import {privileges} from "../constants/privileges-types"
import ListProviders from '../components/providers/ListProviders'
import ComponentAuthority from "../components/policy/ComponentAuthority"
import { initLoading, getAllProviders, clearProviders, remove } from "../actions/providers"

class Provider extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllProviders())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearProviders())
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllProviders({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    handleDelete = providerId => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(providerId))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllProviders({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const loading = false
        const { getFieldDecorator } = this.props.form
        const { providers, history } = this.props

        if (!providers) return <Loading/>

        return (
            <div>
                <PageHeader title={i18n.t('providers')} icon="cluster" />

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
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListProviders dataSource={providers} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_READ_PROVIDER]}>
                        <RouteButton idButton="addProvider" history={history} to="/providers/new" label={i18n.t('add_new_provider')} />
                    </ComponentAuthority>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        providers: state.providers.providers,
        loading: state.providers.loading,
        notification: state.providers.notification
    }
}

const ProviderWrapped = Form.create({})(Provider)

export default connect(mapStateToProps)(ProviderWrapped)