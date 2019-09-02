//3rd's
import React, {Component} from 'react'
import {connect} from 'react-redux'
//components
import {Card, Col, Form, notification, Row, Select, Input, Button, DatePicker, TimePicker} from 'antd'
//actions
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import ListTraces from '../components/traces/ListTraces'
import FilterTraceUtils from "../utils/FilterTraceUtils"
import {getAllTraces, initLoading} from "../actions/traces"
import i18n from "../i18n/i18n";

const {Option} = Select


class Traces extends Component {

    state = {
        page: 0,
        pageSize: 10,
        searchQuery: {},
        filters: FilterTraceUtils.getFilters(),
        filtersSelected: [],
        filterOrder: 0
    }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllTraces({offset: 0, limit: 10, filtersSelected: this.state.filtersSelected}))
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({message, description})
        }
    }

    handlePagination = (page, pageSize) => {
        this.setState({...this.state, page: page - 1, pageSize: pageSize})
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllTraces({offset: page - 1, limit: 10, filtersSelected: this.state.filtersSelected}))
    }

    sendFilters = () => {
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllTraces({offset: 0, limit: 10, filtersSelected: this.state.filtersSelected}))
    }

    updateFiltersSelected = (element) => {
        let orderFilter = this.state.filterOrder
        if (!element.order) {
            element.order = ++orderFilter
        }
        const {filtersSelected} = this.state
        const newFiltersSelected = filtersSelected.filter((e) => e.name !== element.name)
        newFiltersSelected.push(element)
        newFiltersSelected.sort(this.orderFiltersSelected)
        this.setState({...this.state, filtersSelected: newFiltersSelected, filterOrder: orderFilter})
    }

    removeFromFiltersSelected = (element) => {
        const {filtersSelected} = this.state
        const newFiltersSelected = filtersSelected.filter((e) => e.name !== element.name)
        this.setState({...this.state, filtersSelected: newFiltersSelected})
    }

    handleSelectFilter = (value) => {
        const {filters} = this.state
        const elementFound = filters[value]
        this.updateFiltersSelected(elementFound)
    }

    handleChangeFilter = element => valueSelected => {
        element.operationSelected = valueSelected
        this.updateFiltersSelected(element)
    }

    handleChangeValueFilter = element => valueFilter => {
        if (valueFilter && valueFilter.target) {
            element.firstValue = valueFilter.target.value
        } else if (valueFilter) {
            element.firstValue = valueFilter
        } else {
            element.firstValue = ""
        }

        this.updateFiltersSelected(element)
    }

    handleChangeValue2Filter = element => valueFilter => {
        if (valueFilter && valueFilter.target) {
            element.secondValue = valueFilter.target.value
        } else if (valueFilter) {
            element.secondValue = valueFilter
        } else {
            element.secondValue = ""
        }
        this.updateFiltersSelected(element)
    }

    orderFiltersSelected = (a, b) => {
        if (a.order > b.order) {
            return 1
        }

        if (b.order > a.order)
            return -1

        return 0
    }

    validateOperationSelectedToViewInputValue = (operationSelected) => {
        return operationSelected !== "today" && operationSelected !== "yesterday" &&
            operationSelected !== "this week" && operationSelected !== "last week" &&
            operationSelected !== "this month" && operationSelected !== "last month" &&
            operationSelected !== "this year" && operationSelected !== "none" && operationSelected !== "all"
    }

    render() {
        const {traces, loading} = this.props
        const {filters, filtersSelected} = this.state
        const formatDate = "YYYY-MM-DD HH:mm:ss"
        const formatHour = "HH:mm"
        const timeInput = <TimePicker format={formatHour}/>

        if (!traces) return <Loading/>

        return (
            <div>
                <PageHeader title={i18n.t('traces')} icon="sync"/>

                <Row className="search-box">
                    <Card>
                        <Form>
                            <Row>
                                <Col sm={24} md={8}>
                                    <h3>{i18n.t('add_filter')}:</h3>
                                    <Select onChange={this.handleSelectFilter} key="filterSelect">
                                        {
                                            filters.map((element, i) => {
                                                return <Option key={i}>{element.label}</Option>
                                            })
                                        }
                                    </Select>
                                </Col>
                            </Row>
                            {filtersSelected.length > 0 && (
                                <Row>
                                    {
                                        filtersSelected.map((element, i) => {
                                            const options = element.operations.map((operation) => {
                                                return <Option key={operation}>{operation}</Option>
                                            })

                                            return (
                                                <Row key={element.name} gutter={16} justify="left"
                                                     className="heimdall-select-filters">
                                                    <Col sm={24} md={1}>
                                                        <Button key={element.name} icon="close"
                                                                onClick={() => this.removeFromFiltersSelected(element)}/>
                                                    </Col>
                                                    <Col sm={24} md={4}>
                                                        <h3>{element.label}</h3>
                                                    </Col>
                                                    <Col sm={24} md={4}>
                                                        <Select key={i}
                                                                defaultValue={element.operationSelected.length > 0 ? element.operationSelected : "select operation"}
                                                                placeholder="Select operation"
                                                                onChange={this.handleChangeFilter(element)}>{options}</Select>
                                                    </Col>
                                                    {
                                                        this.validateOperationSelectedToViewInputValue(element.operationSelected) &&
                                                        <Col sm={24} md={4}>
                                                            {
                                                                element.type === "date" &&
                                                                <DatePicker
                                                                    showTime={timeInput} format={formatDate}
                                                                    onChange={this.handleChangeValueFilter(element)}
                                                                    style={{width: "100%"}}/>
                                                            }
                                                            {
                                                                element.type === "type" &&
                                                                <Select placeholder="value"
                                                                        defaultValue={element.firstValue.length > 0 ? element.firstValue : "select a value"}
                                                                        onChange={this.handleChangeValueFilter(element)}>
                                                                    {
                                                                        element.possibleValues.map((value) => {
                                                                            return <Option
                                                                                key={value}>{value}</Option>
                                                                        })
                                                                    }
                                                                </Select>
                                                            }
                                                            {
                                                                element.type !== "date" && element.type !== "type" &&
                                                                <Input placeholder="value" value={element.firstValue}
                                                                       onChange={this.handleChangeValueFilter(element)}/>
                                                            }
                                                        </Col>
                                                    }
                                                    {
                                                        element.operationSelected.toUpperCase() === "BETWEEN" &&
                                                        <Col sm={24} md={4}>
                                                            {
                                                                element.type === "date" ?
                                                                    <DatePicker
                                                                        showTime={timeInput} format={formatDate}
                                                                        onChange={this.handleChangeValue2Filter(element)}
                                                                        style={{width: "100%"}}/>
                                                                    :
                                                                    <Input placeholder="value2"
                                                                           value={element.secondValue}
                                                                           onChange={this.handleChangeValue2Filter(element)}/>
                                                            }

                                                        </Col>
                                                    }
                                                </Row>
                                            )
                                        })
                                    }
                                </Row>
                            )}
                            <br/>
                            <div style={{width: "100%", textAlign: "left"}}>
                                <Button id="searchTraces" type="primary" onClick={() => this.sendFilters()} icon="search">{i18n.t('apply_filters')}</Button>
                            </div>
                        </Form>
                    </Card>
                </Row>

                <Row className="h-row bg-white">
                    <ListTraces dataSource={traces} handlePagination={this.handlePagination} loading={loading}/>
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