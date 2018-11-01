//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'

//actions
import { getAllAccessTokens, initLoading, remove } from '../actions/access-tokens'

//components
import { Row, Button, Form, Card, Input, Col, notification } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import ListAccessTokens from '../components/access-tokens/ListAccessTokens'
import Loading from '../components/ui/Loading'
import FloatButton from '../components/ui/FloatButton'

class AccessTokens extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllAccessTokens())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleDelete = (planId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(planId, this.state.page))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllAccessTokens({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllAccessTokens({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { accessTokens, loading, history } = this.props

        if (!accessTokens) return <Loading />

        return (
            <div>
                <PageHeader title="Access Tokens" icon="key" />
                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24} type="flex" justify="start">
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('code')(<Input.Search onSearch={this.onSearchForm} placeholder="Token" />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('app.name')(<Input.Search onSearch={this.onSearchForm} placeholder="App name" />)}
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>
                <Row className="h-row bg-white">
                    <ListAccessTokens dataSource={accessTokens} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />

                    <FloatButton idButton="addAccessToken" history={history} to="/tokens/new" label="Add new Access Token" />
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