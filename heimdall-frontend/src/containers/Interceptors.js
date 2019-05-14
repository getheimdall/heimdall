import React, { Component } from 'react'
import { connect } from 'react-redux'
import { DragDropContext } from 'react-dnd'
import HTML5Backend from 'react-dnd-html5-backend'
import { Row, Col, Form, Select, Icon, Card, Button, notification, Progress, Tag, Alert, Badge } from 'antd'

import i18n from "../i18n/i18n"
import UUID from '../utils/UUID'
import ColorUtils from "../utils/ColorUtils"
import Loading from '../components/ui/Loading'
import {PrivilegeUtils} from "../utils/PrivilegeUtils"
import {privileges} from "../constants/privileges-types"
import { getAllPlans, clearPlans } from '../actions/plans'
import { interceptorSort } from '../utils/InterceptorUtils'
import { receiveQueue, clearQueue } from '../actions/queues'
import {countInterceptorsByCycle} from "../utils/BadgeUtils"
import ComponentAuthority from "../components/policy/ComponentAuthority"
import { getAllOperations, clearOperations } from '../actions/operations'
import { getAllResourcesByApi, clearResources } from '../actions/resources'
import DnDInterceptorType from '../components/interceptors/DnDInterceptorType'
import DropClientInterceptors from '../components/interceptors/DropClientInterceptors'
import { getAllInterceptors, initLoading, getAllInterceptorsTypes, clearInterceptors, clearInterceptorsTypes, saveAll, updateAll, removeAll } from '../actions/interceptors'

const Option = Select.Option

class Interceptors extends Component {

    state = {
        environmentId: 0,
        apiId: 0,
        planId: 0,
        resourceId: 0,
        operationId: 0,
        apiSelected: true,
        planSelected: false,
        resourceSelected: false,
        operationSelected: false,
        candidatesToSave: [],
        candidatesToUpdate: [],
        candidatesToDelete: [],
        showProgress: false,
        progress: 0
    }

    filterByLifeCycle = (interceptors) => {
        if (this.state.operationSelected) {
            return interceptors.filter(interceptor => {
                return (interceptor.referenceId === this.state.operationId && interceptor.lifeCycle === 'OPERATION') ||
                (interceptor.referenceId === this.state.resourceId && interceptor.lifeCycle === 'RESOURCE') ||
                (!this.state.planSelected && interceptor.lifeCycle === 'PLAN') ||
                (this.state.planSelected && interceptor.lifeCycle === 'PLAN' && interceptor.referenceId === this.state.planId)
            })
        }

        if (this.state.resourceSelected) {
            const { operations } = this.props
            let operationsIds = []
            if (operations) {
                operationsIds = operations.map(op => op.id)
            }
            return interceptors.filter(interceptor => {
                return (interceptor.referenceId === this.state.resourceId && interceptor.lifeCycle === 'RESOURCE')||
                    (operationsIds.includes(interceptor.referenceId) && interceptor.lifeCycle === 'OPERATION') ||
                    (!this.state.planSelected && interceptor.lifeCycle === 'PLAN') ||
                    (this.state.planSelected && interceptor.lifeCycle === 'PLAN' && interceptor.referenceId === this.state.planId)
            })
        }

        if (this.state.planSelected) {
            return interceptors.filter(interceptor => {
                return (interceptor.lifeCycle === 'RESOURCE') ||
                    (interceptor.lifeCycle === 'OPERATION') ||
                    (interceptor.referenceId === this.state.planId && interceptor.lifeCycle === 'PLAN')
            })
        }
        return interceptors
    }

    filterByExecutionPoint = (interceptors, point) => {
        return interceptors.filter(interceptor => {
            return interceptor.executionPoint === point
        })
    }

    // filterByPlan

    orderInterceptor = interceptors => {
        return interceptors.sort(interceptorSort)
    }

    componentDidMount() {
        const { id }  = this.props.api
        const query = {'api.id': id, offset: 0}
        this.setState({ ...this.state, apiId: id })
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllInterceptors(query))
        this.props.dispatch(getAllInterceptorsTypes())
        this.props.dispatch(getAllPlans(query))
        this.props.dispatch(getAllResourcesByApi(id))
    }

    componentWillUnmount() {
        this.props.dispatch(clearInterceptors())
        this.props.dispatch(clearInterceptorsTypes())
        this.props.dispatch(clearPlans())
        this.props.dispatch(clearResources())
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({message, description})
        }

        const {showProgress} = this.state
        // progress
        if (newProps.queueCount && newProps.queueCount >= 1) {
            let percentage = Math.round(100 / newProps.queueCount)
            this.setState({showProgress: true, progress: percentage})
        }

        if (newProps.queueCount === 0 && showProgress) {
            notification.info({
                message: i18n.t('interceptors_processed')
            })
            this.setState({showProgress: false, progress: 0})
        }
    }

    componentDidUpdate(prevProps, prevState) {
        if (prevProps.queueCount === 0 && !this.props.queueCount) {
            this.setState({...this.state, candidatesToSave: [], candidatesToDelete: [], candidatesToUpdate: []})
        }

        if (this.props.queueCount === 0) {
            this.props.dispatch(clearQueue())

            this.props.dispatch(clearInterceptors())
            this.props.dispatch(clearPlans())
            this.props.dispatch(clearResources())

            const query = {'api.id': this.props.api.id, offset: 0}
            this.props.dispatch(getAllInterceptors(query))
            this.props.dispatch(getAllPlans(query))
            this.props.dispatch(getAllResourcesByApi(this.props.api.id))
        }
    }

    handleForm = (formObject) => {
        if (formObject.id) {
            formObject.state = 'UPDATE'
            let candidates = this.state.candidatesToUpdate
            if (this.state.candidatesToUpdate.some(updatable => updatable.id === formObject.id)) {
                candidates = this.state.candidatesToUpdate.filter(item => item.id !== formObject.id)
            }

            formObject.uuid = UUID.generate()
            this.setState({...this.state, candidatesToUpdate: [...candidates, formObject]})
        } else {
            formObject.state = 'SAVE'
            let candidates = this.state.candidatesToSave
            if (this.state.candidatesToSave.some(updatable => updatable.uuid === formObject.uuid)) {
                candidates = this.state.candidatesToSave.filter(item => item.uuid !== formObject.uuid)
            }

            formObject.uuid = UUID.generate()
            this.setState({...this.state, candidatesToSave: [...candidates, formObject]})
        }
    }

    handleDelete = (interceptor) => {
        if (interceptor.id) {
            if (interceptor.state) {
                //just remove from candidatesToUpdate
                const candidatesUpdated = this.state.candidatesToUpdate.filter(item => item.uuid !== interceptor.uuid)
                this.setState({...this.state, candidatesToUpdate: candidatesUpdated})
            } else {
                //add to candidatesToDelete
                this.setState({...this.state, candidatesToDelete: [...this.state.candidatesToDelete, interceptor]})
            }
        } else {
            //if enter here its because the user added a new interceptor and decided to remove
            const candidatesUpdated = this.state.candidatesToSave.filter(item => item.uuid !== interceptor.uuid)
            this.setState({...this.state, candidatesToSave: candidatesUpdated})
        }
    }

    handleSelectChange = (type) => (value) => {
        const { planSelected, resourceSelected, operationSelected } = this.state
        switch (type) {
            case 'ENV':
                //dispatch ENV interceptors
                this.setState({...this.state, environmentId: value})
                break;
            case 'PLAN':
                //dispatch PLAN interceptors
                if (value === 0) {
                    const apiSelectedResult = !operationSelected && !resourceSelected
                    this.setState({
                        ...this.state,
                        planSelected: false,
                        planId: value,
                        apiSelected: apiSelectedResult
                    })
                } else {
                    this.setState({
                        ...this.state, planSelected: true, planId: value,
                        apiSelected: false
                    })
                }
                break;
            case 'RES':
                //dispatch RESOURCES interceptors
                this.props.dispatch(clearOperations())
                if (value === 0) {
                    const apiSelectedResult = !planSelected
                    this.setState({
                        ...this.state,
                        resourceSelected: false,
                        resourceId: value,
                        operationSelected: false,
                        operationId: 0,
                        apiSelected: apiSelectedResult
                    })
                } else {
                    this.setState({
                        ...this.state, resourceSelected: true, resourceId: value, operationSelected: false,
                        operationId: 0, apiSelected: false
                    })
                    this.props.dispatch(getAllOperations(this.props.api.id, value))
                }
                break;
            default:
                //dispatch OPERATIONS interceptors
                const test = value !== 0;
                this.setState({...this.state, operationSelected: test, operationId: value})
        }
        this.updateAllParams()
    }

    handleFilterOperation = (input, option) => {
        const path = option.props.title
        try {
            const reg = new RegExp(input, 'i')
            return path.match(reg) !== null
        } catch (e) {
            return true
        }
    }

    updateAllParams = () => {
        const query = {'api.id': this.props.api.id, offset: 0}
        this.props.dispatch(initLoading())
        this.props.dispatch(getAllInterceptors(query))
        this.props.dispatch(getAllInterceptorsTypes())
    }

    discardChanges = () => {
        this.setState({...this.state, candidatesToDelete: [], candidatesToSave: [], candidatesToUpdate: []})
        this.updateAllParams()
    }

    saveChanges = () => {
        const totalInterceptors = this.state.candidatesToSave.length + this.state.candidatesToUpdate.length + this.state.candidatesToDelete.length
        this.props.dispatch(receiveQueue(totalInterceptors))
        if (this.state.candidatesToSave.length > 0 || this.state.candidatesToUpdate.length > 0 || this.state.candidatesToDelete.length > 0){
            this.props.dispatch(initLoading())
        }
        if (this.state.candidatesToSave.length > 0) this.props.dispatch(saveAll(this.state.candidatesToSave))
        if (this.state.candidatesToUpdate.length > 0) this.props.dispatch(updateAll(this.state.candidatesToUpdate))
        if (this.state.candidatesToDelete.length > 0) this.props.dispatch(removeAll(this.state.candidatesToDelete))
    }

    addedOperationCandidateToIgnore = interceptor => {
        if (interceptor.lifeCycle !== 'OPERATION') {
            interceptor.candidateToIgnoreId = this.state.operationId
        }

        return interceptor
    }

    render() {
        const { interceptors, api, operations } = this.props

        let environmentPath
        let operationPath

        if (this.state.environmentId !== 0 && api && api.environments){
            const env = api.environments.find(e => e.id === this.state.environmentId)
            if (env) {
                environmentPath = env.inboundURL
            } else {
                environmentPath = ''
            }
        }

        if (this.state.operationId !== 0 && operations) {
            const op = operations.find(o => o.id === this.state.operationId)
            if (op) {
                operationPath = op.path
            } else {
                operationPath = ''
            }
        }

        let allInterceptors

        let interceptorsPreFiltered
        let interceptorsPostFiltered

        if (this.props.interceptors) {
            allInterceptors = interceptors.content
            allInterceptors = this.filterByLifeCycle(allInterceptors)
            if (this.state.planSelected || this.state.resourceSelected || this.state.operationSelected) {
                allInterceptors = allInterceptors.concat(interceptors.content.filter(item => item.lifeCycle === 'API'))
            }

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

            const interceptorsFirst = this.filterByExecutionPoint(allInterceptors, 'FIRST')
            const interceptorsSecond = this.filterByExecutionPoint(allInterceptors, 'SECOND')
            let interceptorsPreOrdered = this.orderInterceptor(interceptorsFirst)
            let interceptorsPostOrdered = this.orderInterceptor(interceptorsSecond)

            if (this.state.operationSelected) {
                interceptorsPreOrdered = interceptorsPreOrdered.map(interceptor => this.addedOperationCandidateToIgnore(interceptor))
                interceptorsPostOrdered = interceptorsPostOrdered.map(interceptor => this.addedOperationCandidateToIgnore(interceptor))
            }

            interceptorsPreFiltered = interceptorsPreOrdered
            interceptorsPostFiltered = interceptorsPostOrdered

        }

        const hasNoChanges = this.state.candidatesToSave.length === 0 && this.state.candidatesToUpdate.length === 0 && this.state.candidatesToDelete.length === 0
        const canAddInterceptor =
            (this.state.apiSelected || this.state.planSelected || this.state.resourceSelected || this.state.operationSelected)
            && (interceptorsPreFiltered && interceptorsPostFiltered)
            && (this.props.plans || !this.props.resources)

        return (
            <div>
                <Alert message={i18n.t('select_any_option_to_enable_the_interceptors')} type="info" closable
                       style={{marginBottom: 10}} showIcon/>
                <Card
                    title={i18n.t('choose_your_interceptors')}
                    style={{marginBottom: 20}}
                    className="inside-shadow"
                    extra={
                        <span>{environmentPath}{api.basePath}{operationPath}</span>
                    }
                >
                    <Form>
                        <Row gutter={20} type="flex" justify="space-between" align="middle">
                            <Col sm={24} md={6}>
                                <Form.Item label={i18n.t('environments')}>
                                    <Select defaultValue={0} onChange={this.handleSelectChange('ENV')}>
                                        <Option value={0}>{i18n.t('all')}</Option>
                                        {api && api.environments.map((env, index) => (
                                            <Option key={index} value={env.id}>{env.name}</Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>
                            <Col sm={24} md={6}>
                                <Form.Item label={i18n.t('plans')}>
                                    <Select defaultValue={0} onChange={this.handleSelectChange('PLAN')} disabled={!this.props.plans} className="heimdall-select-filter-complete">
                                        <Option value={0}>{i18n.t('all')}</Option>
                                        {this.props.plans && this.props.plans.content.map((plan, index) => (
                                            <Option key={index} value={plan.id}>{plan.name}
                                                <Badge title="Numbers of interceptors" className="heimdall-badge-interceptors-count" count={countInterceptorsByCycle(interceptors && interceptors.content, 'PLAN', plan.id)}/>
                                            </Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>
                            <Col sm={24} md={6}>
                                <Form.Item label={i18n.t('resources')}>
                                    <Select defaultValue={0} onChange={this.handleSelectChange('RES')} disabled={!this.props.resources} className="heimdall-select-filter-complete">
                                        <Option value={0}>{i18n.t('all')}</Option>
                                        {this.props.resources && this.props.resources.map((res, index) => (
                                            <Option key={index} value={res.id}>
                                                <Badge title="Numbers of interceptors" className="heimdall-badge-interceptors-count" count={countInterceptorsByCycle(interceptors && interceptors.content, 'RESOURCE', res.id)}/>
                                                {res.name}</Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>

                            <Col sm={24} md={6}>
                                <Form.Item label={i18n.t('operations')}>
                                    <Select showSearch value={this.state.operationId} onChange={this.handleSelectChange('OP')} disabled={!this.props.operations} className="heimdall-select-filter-complete" filterOption={this.handleFilterOperation}>
                                        <Option value={0}>{i18n.t('all')}</Option>
                                        {this.props.operations && this.props.operations.map((op, index) => (
                                            <Option key={index} value={op.id} title={op.method + " " + op.path}>
                                                <Badge title="Numbers of interceptors" className="heimdall-badge-interceptors-count" count={countInterceptorsByCycle(interceptors && interceptors.content, 'OPERATION', op.id)}/>
                                                <Tag color={ColorUtils.getColorMethod(op.method)}>{op.method}</Tag> {op.path}
                                            </Option>
                                        ))}
                                    </Select>
                                </Form.Item>
                            </Col>
                        </Row>
                    </Form>
                </Card>

                <Card title={i18n.t('interceptors')}
                      extra={
                          <Row type="flex" justify="center">
                              {this.state.showProgress &&
                              <Progress type="circle" percent={this.state.progress} width={30} status="active"
                                        style={{marginRight: 10}}/>}
                              <Tag color="#989898">{i18n.t('can_drag')}</Tag>
                              <Tag color="#ffa613">{i18n.t('api')}</Tag>
                              <Tag color="#c3cc93">{i18n.t('plan')}</Tag>
                              <Tag color="#8edce0">{i18n.t('resource')}</Tag>
                              <Tag color="#607d8b">{i18n.t('operation')}</Tag>
                          </Row>}
                >
                    <div>
                        <Row style={{marginBottom: 20}}>
                            {!this.props.interceptorTypes && <Loading/>}
                            {this.props.interceptorTypes && this.props.interceptorTypes.map((interceptor, index) => (
                                <DnDInterceptorType key={index}
                                    type={interceptor.type}
                                    icon='code-o'
                                    canAddInterceptor={PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_INTERCEPTOR, privileges.PRIVILEGE_UPDATE_INTERCEPTOR]) && canAddInterceptor}
                                    color={PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_INTERCEPTOR, privileges.PRIVILEGE_UPDATE_INTERCEPTOR]) && canAddInterceptor && '#989898'}
                                    apiId={this.state.apiId !== 0 && this.state.apiId}
                                    environmentId={this.state.environmentId !== 0 && this.state.environmentId}
                                    planId={this.state.planId !== 0 && this.state.planId}
                                    resourceId={this.state.resourceId !== 0 && this.state.resourceId}
                                    operationId={this.state.operationId !== 0 && this.state.operationId}
                                    handleForm={this.handleForm}
                                />
                            ))}
                        </Row>
                        <DropClientInterceptors interceptors={interceptorsPreFiltered} description={i18n.t('request')}
                                                loading={this.props.loading} executionPoint={'FIRST'}
                                                handleForm={this.handleForm} handleDelete={this.handleDelete}/>
                        <br/>
                        <DropClientInterceptors interceptors={interceptorsPostFiltered} description={i18n.t('response')}
                                                loading={this.props.loading} executionPoint={'SECOND'}
                                                handleForm={this.handleForm} handleDelete={this.handleDelete}/>
                    </div>
                </Card>
                <Row>
                    <Col md={12}>
                        <Row className="h-row" type="flex" justify="start">

                        </Row>
                    </Col>
                    <Col md={12}>
                        <Row className="h-row" type="flex" justify="end">
                            <Button id="discardInterceptors" className="card-button" type="danger" disabled={hasNoChanges} onClick={this.discardChanges}>
                                <Icon type="delete" /> {i18n.t('discard')}
                            </Button>
                            <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_INTERCEPTOR]}>
                                <Button id="saveInterceptors" className="card-button" type="primary" disabled={hasNoChanges} onClick={this.saveChanges}>
                                    <Icon type="save" /> {i18n.t('save_changes')}
                                </Button>
                            </ComponentAuthority>
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
