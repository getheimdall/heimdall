import React, { Component } from 'react'
import { connect } from 'react-redux'
import { DragDropContext } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';

import DropClientInterceptors from '../components/interceptors/DropClientInterceptors'
import { interceptorSort } from '../utils/InterceptorUtils'
import UUID from '../utils/UUID'

import { getAllInterceptors, initLoading, getAllInterceptorsTypes, clearInterceptors, clearInterceptorsTypes, saveAll, updateAll, removeAll } from '../actions/interceptors';
import { getAllPlans, clearPlans } from '../actions/plans';
import { receiveQueue, clearQueue } from '../actions/queues'

import { Row, Col, Form, Select, Icon, Card, Button, notification, Progress, Tag, Alert } from 'antd'
import Loading from '../components/ui/Loading';
import { getAllResourcesByApi, clearResources } from '../actions/resources';
import { getAllOperations, clearOperations } from '../actions/operations';
import DnDInterceptorType from '../components/interceptors/DnDInterceptorType';

const Option = Select.Option

class Interceptors extends Component {

    state = {
        environmentId: 0,
        planId: 0,
        resourceId: 0,
        operationId: 0,
        planSelected: false,
        resourceSelected: false,
        operationSelected: false,
        candidatesToSave: [],
        candidatesToUpdate: [],
        candidatesToDelete: [],
        showProgress: false,
        progress: 0
    }

    filterByExecutionPoint = (interceptors, point) => {
        return interceptors.filter(interceptor => {
            return interceptor.executionPoint === point
        })
    }

    orderInterceptor = interceptors => {
        return interceptors.sort(interceptorSort)
    }

    componentDidMount() {
        const query = { 'api.id': this.props.api.id, offset: 0 }
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllInterceptors(query))
        this.props.dispatch(getAllInterceptorsTypes())
        this.props.dispatch(getAllPlans(query))
        this.props.dispatch(getAllResourcesByApi(this.props.api.id))
    }

    componentWillUnmount() {
        this.props.dispatch(clearInterceptors())
        this.props.dispatch(clearInterceptorsTypes())
        this.props.dispatch(clearPlans())
        this.props.dispatch(clearResources())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }

        const { showProgress } = this.state
        // progress
        if (newProps.queueCount && newProps.queueCount >= 1) {
            let percentage = Math.round(100 / newProps.queueCount)
            this.setState({ showProgress: true, progress: percentage })
        }

        if (newProps.queueCount === 0 && showProgress) {
            notification.info({
                message: 'Interceptors processed'
            })
            this.setState({ showProgress: false, progress: 0 })
        }
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevProps.queueCount === 0 && !this.props.queueCount) {
            this.setState({ ...this.state, candidatesToSave: [], candidatesToDelete: [], candidatesToUpdate: [] })
        }

        if (this.props.queueCount === 0) {
            this.props.dispatch(clearQueue())

            this.props.dispatch(clearInterceptors())
            this.props.dispatch(clearPlans())
            this.props.dispatch(clearResources())

            const query = { 'api.id': this.props.api.id, offset: 0 }
            this.props.dispatch(getAllInterceptors(query))
            this.props.dispatch(getAllPlans(query))
            this.props.dispatch(getAllResourcesByApi(this.props.api.id))
        }
    }

    handleForm = (formObject) => {
        if (formObject.id) {
            formObject.status = 'UPDATE'
            let candidates = this.state.candidatesToUpdate
            if (this.state.candidatesToUpdate.some(updatable => updatable.id === formObject.id)) {
                candidates = this.state.candidatesToUpdate.filter(item => item.id !== formObject.id)
            }

            formObject.uuid = UUID.generate()
            this.setState({ ...this.state, candidatesToUpdate: [...candidates, formObject] })
        } else {
            formObject.status = 'SAVE'
            let candidates = this.state.candidatesToSave
            if (this.state.candidatesToSave.some(updatable => updatable.uuid === formObject.uuid)) {
                candidates = this.state.candidatesToSave.filter(item => item.uuid !== formObject.uuid)
            }

            formObject.uuid = UUID.generate()
            this.setState({ ...this.state, candidatesToSave: [...candidates, formObject] })
        }
    }

    handleDelete = (interceptor) => {
        if (interceptor.id) {
            if (interceptor.status) {
                //just remove from candidatesToUpdate
                const candidatesUpdated = this.state.candidatesToUpdate.filter(item => item.uuid !== interceptor.uuid)
                this.setState({ ...this.state, candidatesToUpdate: candidatesUpdated })
            } else {
                //add to candidatesToDelete
                this.setState({ ...this.state, candidatesToDelete: [...this.state.candidatesToDelete, interceptor] })
            }
        } else {
            //if enter here its because the user added a new interceptor and decided to remove
            const candidatesUpdated = this.state.candidatesToSave.filter(item => item.uuid !== interceptor.uuid)
            this.setState({ ...this.state, candidatesToSave: candidatesUpdated })
        }
    }

    handleSelectChange = (type) => (value) => {
        switch (type) {
            case 'ENV':
                //dispatch ENV interceptors
                if (value === 0) {
                    this.setState({ ...this.state, environmentId: value })
                } else {
                    this.setState({ ...this.state, environmentId: value })
                }
                break;
            case 'PLAN':
                //dispatch PLAN interceptors
                if (value === 0) {
                    this.setState({ ...this.state, planSelected: false, planId: value })
                } else {
                    this.setState({ ...this.state, planSelected: true, planId: value })
                }
                break;
            case 'RES':
                //dispatch RESOURCES interceptors
                this.props.dispatch(clearOperations())
                if (value === 0) {
                    this.setState({ ...this.state, resourceSelected: false, resourceId: value })
                } else {
                    this.setState({ ...this.state, resourceSelected: true, resourceId: value })
                    this.props.dispatch(getAllOperations(this.props.api.id, value))
                }
                break;
            default:
                //dispatch OPERATIONS interceptors
                if (value === 0) {
                    this.setState({ ...this.state, operationSelected: false, operationId: value })
                } else {
                    this.setState({ ...this.state, operationSelected: true, operationId: value })
                }
        }
    }

    discardChanges = () => {
        this.setState({ ...this.state, candidatesToDelete: [], candidatesToSave: [], candidatesToUpdate: [] })
    }

    saveChanges = () => {
        const totalInterceptors = this.state.candidatesToSave.length + this.state.candidatesToUpdate.length + this.state.candidatesToDelete.length
        this.props.dispatch(receiveQueue(totalInterceptors))

        if (this.state.candidatesToSave.length > 0) this.props.dispatch(saveAll(this.state.candidatesToSave))
        if (this.state.candidatesToUpdate.length > 0) this.props.dispatch(updateAll(this.state.candidatesToUpdate))
        if (this.state.candidatesToDelete.length > 0) this.props.dispatch(removeAll(this.state.candidatesToDelete))
    }

    render() {

        const { interceptors, api } = this.props
        let allInterceptors

        let interceptorsPreFiltered
        let interceptorsPostFiltered

        if (this.props.interceptors) {
            allInterceptors = interceptors.content
            if (this.state.candidatesToSave.length > 0) {
                allInterceptors = allInterceptors.concat(this.state.candidatesToSave)
            }

            if (this.state.candidatesToUpdate.length > 0) {
                allInterceptors = allInterceptors.filter(item => !this.state.candidatesToUpdate.some(updatable => updatable.id === item.id))
                allInterceptors = allInterceptors.concat(this.state.candidatesToUpdate)
            }

            if (this.state.candidatesToDelete.length > 0) {
                allInterceptors = allInterceptors.filter(item => !this.state.candidatesToDelete.some(updatable => updatable.id === item.id))
            }

            let interceptorsFirst = this.filterByExecutionPoint(allInterceptors, 'FIRST')
            let interceptorsSecond = this.filterByExecutionPoint(allInterceptors, 'SECOND')
            let interceptorsPreOrdered = this.orderInterceptor(interceptorsFirst)
            let interceptorsPostOrdered = this.orderInterceptor(interceptorsSecond)

            if (this.state.environmentId !== 0) {
                const environmentsPreFiltered = interceptorsPreOrdered.filter(item => (item.environment && item.environment.id === this.state.environmentId) || item.environment === null)
                const environmentsPostFiltered = interceptorsPostOrdered.filter(item => (item.environment && item.environment.id === this.state.environmentId) || item.environment === null)

                interceptorsPreOrdered = environmentsPreFiltered
                interceptorsPostOrdered = environmentsPostFiltered
            }

            if (this.state.planId !== 0) {
                const plansPreFiltered = interceptorsPreOrdered.filter(item => (item.lifeCycle === 'PLAN' && item.referenceId === this.state.planId) || (item.lifeCycle !== 'PLAN'))
                const plansPostFiltered = interceptorsPostOrdered.filter(item => (item.lifeCycle === 'PLAN' && item.referenceId === this.state.planId) || (item.lifeCycle !== 'PLAN'))

                interceptorsPreOrdered = plansPreFiltered
                interceptorsPostOrdered = plansPostFiltered
            }

            if (this.state.resourceId !== 0) {
                const resourcesPreFiltered = interceptorsPreOrdered.filter(item => (item.lifeCycle === 'RESOURCE' && item.referenceId === this.state.resourceId) || (item.lifeCycle !== 'RESOURCE'))
                const resourcesPostFiltered = interceptorsPostOrdered.filter(item => (item.lifeCycle === 'RESOURCE' && item.referenceId === this.state.resourceId) || (item.lifeCycle !== 'RESOURCE'))

                interceptorsPreOrdered = resourcesPreFiltered
                interceptorsPostOrdered = resourcesPostFiltered
            }

            if (this.state.operationId !== 0) {
                const operationsPreFiltered = interceptorsPreOrdered.filter(item => (item.lifeCycle === 'OPERATION' && item.referenceId === this.state.operationId) || (item.lifeCycle !== 'OPERATION'))
                const operationsPostFiltered = interceptorsPostOrdered.filter(item => (item.lifeCycle === 'OPERATION' && item.referenceId === this.state.operationId) || (item.lifeCycle !== 'OPERATION'))

                interceptorsPreOrdered = operationsPreFiltered
                interceptorsPostOrdered = operationsPostFiltered
            }

            interceptorsPreFiltered = interceptorsPreOrdered
            interceptorsPostFiltered = interceptorsPostOrdered
        }

        const hasNoChanges = this.state.candidatesToSave.length === 0 && this.state.candidatesToUpdate.length === 0 && this.state.candidatesToDelete.length === 0
        const canAddInterceptor =
            (this.state.planSelected || this.state.resourceSelected || this.state.operationSelected)
            && (interceptorsPreFiltered && interceptorsPostFiltered)
            && (this.props.plans || !this.props.resources)

        return (
            <div>
                <Alert message="Select any option to enable the interceptors" type="info" closable style={{ marginBottom: 10 }} showIcon />
                <Card
                    title="Choose your Interceptors"
                    style={{ marginBottom: 20 }}
                    className="inside-shadow"
                >
                    <Form>
                        <Row gutter={20} type="flex" justify="space-between" align="middle">
                            <Col sm={24} md={6}>
                                <Form.Item label="Environments">
                                    <Select defaultValue={0} onChange={this.handleSelectChange('ENV')}>
                                        <Option value={0}>All</Option>
                                        {api && api.environments.map((env, index) => (
                                            <Option key={index} value={env.id}>{env.name}</Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>
                            <Col sm={24} md={6}>
                                <Form.Item label="Plans">
                                    <Select defaultValue={0} onChange={this.handleSelectChange('PLAN')} disabled={!this.props.plans}>
                                        <Option value={0}>All</Option>
                                        {this.props.plans && this.props.plans.content.map((plan, index) => (
                                            <Option key={index} value={plan.id}>{plan.name}</Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>
                            <Col sm={24} md={6}>
                                <Form.Item label="Resources">
                                    <Select defaultValue={0} onChange={this.handleSelectChange('RES')} disabled={!this.props.resources}>
                                        <Option value={0}>All</Option>
                                        {this.props.resources && this.props.resources.map((res, index) => (
                                            <Option key={index} value={res.id}>{res.name}</Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>

                            <Col sm={24} md={6}>
                                <Form.Item label="Operations">
                                    <Select defaultValue={0} onChange={this.handleSelectChange('OP')} disabled={!this.props.operations}>
                                        <Option value={0}>All</Option>
                                        {this.props.operations && this.props.operations.map((op, index) => (
                                            <Option key={index} value={op.id}>{op.path}</Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Form>
                </Card>

                <Card title="Interceptors"
                    extra={
                        <Row type="flex" justify="center">
                            {this.state.showProgress && <Progress type="circle" percent={this.state.progress} width={30} status="active" style={{marginRight: 10}} />}
                            <Tag color="#989898">Can Drag</Tag>
                            <Tag color="#c3cc93">Plan</Tag>
                            <Tag color="#8edce0">Resource</Tag>
                            <Tag color="#607d8b">Operation</Tag>
                        </Row>}
                >
                    <div>
                        <Row style={{ marginBottom: 20 }}>
                            {!this.props.interceptorTypes && <Loading />}
                            {this.props.interceptorTypes && this.props.interceptorTypes.map((interceptor, index) => (
                                <DnDInterceptorType key={index}
                                    type={interceptor.type}
                                    icon='code-o'
                                    canAddInterceptor={canAddInterceptor}
                                    color={canAddInterceptor && '#989898'}
                                    environmentId={this.state.environmentId !== 0 && this.state.environmentId}
                                    planId={this.state.planId !== 0 && this.state.planId}
                                    resourceId={this.state.resourceId !== 0 && this.state.resourceId}
                                    operationId={this.state.operationId !== 0 && this.state.operationId}
                                    handleForm={this.handleForm}
                                />
                            ))}
                        </Row>
                        <DropClientInterceptors interceptors={interceptorsPreFiltered} description="REQUEST" loading={this.props.loading} executionPoint={'FIRST'} handleForm={this.handleForm} handleDelete={this.handleDelete} />
                        <br />
                        <DropClientInterceptors interceptors={interceptorsPostFiltered} description="RESPONSE" loading={this.props.loading} executionPoint={'SECOND'} handleForm={this.handleForm} handleDelete={this.handleDelete} />
                    </div>
                </Card>
                <Row>
                    <Col md={12}>
                        <Row className="h-row" type="flex" justify="start">

                        </Row>
                    </Col>
                    <Col md={12}>
                        <Row className="h-row" type="flex" justify="end">
                            <Button className="card-button" type="danger" disabled={hasNoChanges} onClick={this.discardChanges}>
                                <Icon type="delete" /> Discard
                            </Button>
                            <Button className="card-button" type="primary" disabled={hasNoChanges} onClick={this.saveChanges}>
                                <Icon type="save" /> Save Changes
                            </Button>
                        </Row>
                    </Col>
                </Row>
            </div>
        )
    }
}

const WrappedInterceptor = DragDropContext(HTML5Backend)(Interceptors)

const mapStateToProps = state => {
    return {
        interceptors: state.interceptors.interceptors,
        loading: state.interceptors.loading,
        interceptorTypes: state.interceptors.interceptorTypes,
        plans: state.plans.plans,
        resources: state.resources.resources,
        operations: state.operations.operations,
        notification: state.interceptors.notification,
        queueCount: state.queue.count
    }
}

export default connect(mapStateToProps)(WrappedInterceptor)