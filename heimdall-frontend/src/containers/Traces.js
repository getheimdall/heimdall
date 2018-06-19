//3rd's
import React, {Component} from 'react'
import {connect} from 'react-redux'

//actions
import {getAllTraces, initLoading} from "../actions/traces";

//components
import {Button, Card, Col, Form, Input, notification, Row} from 'antd'
import PageHeader from '../components/ui/PageHeader'
import ListTraces from '../components/traces/ListTraces'
import Loading from '../components/ui/Loading'


class Traces extends Component {

    state = { page: 0, pageSize: 10, searchQuery: {}, traces: [{}, {}, {}]  }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllTraces())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handlePagination = (page, pageSize) => {
        this.setState({ ...this.state, page: page - 1, pageSize: pageSize })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllTraces({ offset: page - 1, limit: 10, ...this.state.searchQuery }))
    }

    onSearchForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                this.props.dispatch(initLoading())
                this.props.dispatch(getAllTraces({ offset: 0, limit: 10, ...payload }))
                this.setState({ ...this.state, searchQuery: payload })
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { traces, loading } = this.props

        if (!traces) return <Loading />

        return (
            <div>
                <PageHeader title="Traces" icon="sync" />

                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row gutter={24}>
                                <Col sm={24} md={4}>
                                    {getFieldDecorator('method')(<Input.Search onSearch={this.onSearchForm} placeholder="Method" />)}
                                </Col>
                                <Col sm={24} md={4}>
                                    {getFieldDecorator('status')(<Input.Search onSearch={this.onSearchForm} placeholder="Status response" />)}
                                </Col>
                                <Col sm={24} md={4}>
                                    {getFieldDecorator('path')(<Input.Search onSearch={this.onSearchForm} placeholder="Path" />)}
                                </Col>
                                <Col sm={24} md={4}>
                                    {getFieldDecorator('date')(<Input.Search onSearch={this.onSearchForm} placeholder="yyyy/mm/dd" />)}
                                </Col>
                                <Col sm={24} md={4}>
                                    {getFieldDecorator('level')(<Input.Search onSearch={this.onSearchForm} placeholder="Level" />)}
                                </Col>
                                <Col sm={24} md={4}>
                                    <Row type="flex" justify="end">
                                        <Button className="card-button" type="primary" icon="search" onClick={this.onSearchForm}>Apply Filters</Button>
                                    </Row>
                                </Col>
                            </Row>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListTraces handlePagination={this.handlePagination} loading={loading} />
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        traces: state.traces.traces,
        loading: state.traces.loading,
        notification: state.traces.notification
    }
}

const TracesWrapped = Form.create({})(Traces)

export default connect(mapStateToProps)(TracesWrapped)