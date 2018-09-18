//3rd's
import React, { Component } from 'react'
import { connect } from 'react-redux'

//actions
import { getAllPlans, initLoading, remove, clearPlans } from '../actions/plans'

//components
import { Row, Button, Form, Card, Input, Col, notification } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import ListPlans from '../components/plans/ListPlans'
import Loading from '../components/ui/Loading'
import FloatButton from '../components/ui/FloatButton'

class Plans extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {} }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllPlans())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearPlans())
    }

    handleDelete = (planId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(remove(planId))
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllPlans({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllPlans({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { plans, loading, history } = this.props

        if (!plans) return <Loading />

        return (
            <div>
                <PageHeader title="Plans" icon="profile" />

                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24}>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('name')(<Input.Search onSearch={this.onSearchForm} placeholder="name" />)}
                                </Col>
                                <Col sm={24} md={5}>
                                    {getFieldDecorator('description')(<Input.Search onSearch={this.onSearchForm} placeholder="description" />)}
                                </Col>
                                <Col sm={24} md={14}>
                                    <Row type="flex" justify="end">
                                        <Button id="searchPlans" className="card-button" type="primary" icon="search" onClick={this.onSearchForm}>Search</Button>
                                    </Row>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListPlans dataSource={plans} handleDelete={this.handleDelete} handlePagination={this.handlePagination} loading={loading} />

                    <FloatButton idButton="addPlan" history={history} to="/plans/new" label="Add new PLAN" />
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        plans: state.plans.plans,
        loading: state.plans.loading,
        notification: state.plans.notification
    }
}

const PlansWrapped = Form.create({})(Plans)

export default connect(mapStateToProps)(PlansWrapped)