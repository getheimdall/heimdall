//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'

//actions
import { getAllDevelopers, initLoading, clearDevelopers, remove } from '../actions/developers'

//components
import { Row, Button, Form, Input, Col, Card, notification } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import ListDevelopers from '../components/developers/ListDevelopers'
import Loading from '../components/ui/Loading'
import FloatButton from '../components/ui/FloatButton'
import ComponentAuthority from "../components/ComponentAuthority";
import {privileges} from "../constants/privileges-types";

class Developers extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllDevelopers())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleDelete = (developerId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(clearDevelopers())
        this.props.dispatch(remove(developerId, {offset: this.state.page, limit: this.state.pageSize}))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllDevelopers({offset: page - 1, limit: 10, ...this.state.searchQuery}))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllDevelopers({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { developers, loading, history } = this.props

        if (!developers) return <Loading />

        return (
            <div>
                <PageHeader title="Developers" icon="code" />
                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24}>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('name')(<Input.Search onSearch={this.onSearchForm} placeholder="name" />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('email')(<Input.Search onSearch={this.onSearchForm} placeholder="email" />)}
                                </Col>
                                <Col sm={24} md={14}>
                                    <Row type="flex" justify="end">
                                        <Button id="searchDeveloper" className="card-button" type="primary" icon="search" onClick={this.onSearchForm}>Search</Button>
                                    </Row>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">

                    <ListDevelopers dataSource={developers} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />

                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_UPDATE_INTERCEPTOR]}>
                        <FloatButton idButton="addDeveloper" history={history} to="/developers/new" label="Add new Developer" />
                    </ComponentAuthority>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        developers: state.developers.developers,
        loading: state.developers.loading,
        notification: state.developers.notification
    }
}

const DevelopersWrapped = Form.create({})(Developers)

export default connect(mapStateToProps)(DevelopersWrapped)