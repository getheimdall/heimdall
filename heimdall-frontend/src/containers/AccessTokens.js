//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'
//components
import {Row, Form, Card, Input, Col, notification} from 'antd'
//actions
import {getAllAccessTokens, initLoading, remove} from '../actions/access-tokens'

//components
import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import RouteButton from '../components/ui/RouteButton'
import { privileges } from "../constants/privileges-types"
import ComponentAuthority from "../components/policy/ComponentAuthority"
import ListAccessTokens from '../components/access-tokens/ListAccessTokens'

class AccessTokens extends Component {

    state = {page: 0, pageSize: 10, searchQuery: {}}

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllAccessTokens())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({message, description})
        }
    }

    handleDelete = (planId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(planId, this.state.page))
    }

    handlePagination = (page, pageSize) => {
        this.setState({...this.state, page: page - 1, pageSize: pageSize})
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllAccessTokens({offset: page - 1, limit: 10, ...this.state.searchQuery}))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllAccessTokens({offset: 0, limit: 10, ...payload}))
                this.setState({...this.state, searchQuery: payload})
            }
        });
    }

    render() {
        const {getFieldDecorator} = this.props.form
        const {accessTokens, loading, history} = this.props

        if (!accessTokens) return <Loading/>

        return (
            <div>
                <PageHeader title={i18n.t('access_tokens')} icon="key"/>
                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24} type="flex" justify="start">
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('code')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('token')} />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('app.name')(<Input.Search onSearch={this.onSearchForm} placeholder={i18n.t('app_name')} />)}
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>
                <Row className="h-row bg-white">
                    <ListAccessTokens dataSource={accessTokens} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading}/>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_ACCESSTOKEN]}>
                        <RouteButton idButton="addAccessToken" history={history} to="/tokens/new" label={i18n.t('add_new_access_token')} />
                    </ComponentAuthority>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        accessTokens: state.accessTokens.accessTokens,
        loading: state.accessTokens.loading,
        notification: state.accessTokens.notification
    }
}

const AccessTokensWrapped = Form.create({})(AccessTokens)

export default connect(mapStateToProps)(AccessTokensWrapped)