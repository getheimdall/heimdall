import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Form, Input, Col, Select, Switch, Button } from 'antd'

import i18n from "../../i18n/i18n"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import { getTemplate } from '../../utils/InterceptorUtils'
import {privileges} from "../../constants/privileges-types"

const FormItem = Form.Item
const { TextArea } = Input

class InterceptorForm extends Component {

    state = {
        ignoredOperations: []
    }

    componentDidMount() {
        this.props.onRef(this)

        const { interceptor } = this.props

        if (interceptor) {
            this.setState({
                ...this.state,
                ignoredOperations: interceptor.ignoredOperations
            })
        }
    }

    componentWillUnmount() {
        this.props.onRef(undefined)
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                // if (payload.plans) {
                //     const plans = payload.plans;
                //     payload.plans = plans.map((planId) => ({ id: planId }))
                // }
                if (payload.environment) {
                    payload.environment = { id: payload.environment }
                }
                this.props.handleForm(payload)
                this.props.closeModal()
            }
        });
    }

    handleLifeCycle = (value) => {
        let referenceValue = 0;
        if (value === 'API') {
            referenceValue = this.props.apiId
        } else if (value === 'PLAN') {
            referenceValue = this.props.planId
        } else if (value === 'RESOURCE') {
            referenceValue = this.props.resourceId
        } else if (value === 'OPERATION') {
            referenceValue = this.props.operationId
        }

        this.props.form.setFieldsValue({
            referenceId: referenceValue,
        });
    }

    formatContent = (type) => {
        return getTemplate(type)
    }

    isIgnore = () => {
        const { interceptor } = this.props

        if (!interceptor) {
            return false
        }

        return !!(interceptor.candidateToIgnoreId && this.state.ignoredOperations.find(operation => operation === interceptor.candidateToIgnoreId))
    }

    handleOnChangeCandidateToIgnore = value => {
        const { interceptor } = this.props

        if (value) {
            this.setState({ ...this.state, ignoredOperations: this.state.ignoredOperations.concat([interceptor.candidateToIgnoreId]) })
        } else {
            this.setState({ ...this.state, ignoredOperations: this.state.ignoredOperations.filter(ignored => ignored !== interceptor.candidateToIgnoreId) })
        }

    }

    render() {
        const { getFieldDecorator, getFieldValue } = this.props.form
        
        const {
            apiId,
            interceptor,
            planId,
            resourceId,
            operationId,
            executionPoint,
            type,
            environmentId
        } = this.props


        let lifeCycleInitial
        let referenceId

        if (apiId) {
            lifeCycleInitial = 'API'
            referenceId = apiId
        }

        if (planId) {
            lifeCycleInitial = 'PLAN'
            referenceId = planId
        }

        if (resourceId) {
            lifeCycleInitial = 'RESOURCE'
            referenceId = resourceId
        }

        if (operationId) {
            lifeCycleInitial = 'OPERATION'
            referenceId = operationId
        }

        let status
        if (getFieldValue('status') === undefined) {
            if (interceptor) {
                status = interceptor.status
            } else {
                status = true
            }
        } else {
            status = getFieldValue('status')
        }

        return (
            <Row>
                <Form>
                    {interceptor && getFieldDecorator('id', { initialValue: interceptor.id })(<Input type='hidden' />)}
                    {interceptor && interceptor.uuid && getFieldDecorator('uuid', { initialValue: interceptor.uuid })(<Input type='hidden' />)}
                    {getFieldDecorator('referenceId', { initialValue: interceptor ? interceptor.referenceId : referenceId })(<Input type='hidden' />)}
                    {environmentId && getFieldDecorator('environment', { initialValue: environmentId })(<Input type='hidden' />)}
                    {getFieldDecorator('order', { initialValue: interceptor && interceptor.order ? interceptor.order : this.props.order})(<Input type='hidden' />)}
                    {getFieldDecorator('type', { initialValue: type})(<Input type='hidden' />)}
                    {getFieldDecorator('executionPoint', { initialValue: executionPoint })(<Input type='hidden' />)}
                    {getFieldDecorator('ignoredOperations', { initialValue: this.state.ignoredOperations })(<Input type='hidden' />)}

                    <Row gutter={24}>
                        <Col sm={24} md={interceptor && interceptor.candidateToIgnoreId ? 15 : 20} >
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: interceptor && interceptor.name,
                                        rules: [
                                            { required: true, message: i18n.t('please_define_name') }
                                        ]
                                    })(<Input required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]) && status)}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={4} >
                            <FormItem label={i18n.t('status')}>
                                {
                                    getFieldDecorator('status', {
                                        initialValue: interceptor && interceptor.status
                                    })(<Switch disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}
                                               defaultChecked={interceptor ? interceptor.status : true}>
                                        </Switch>
                                    )
                                }
                            </FormItem>
                        </Col>
                        {
                            interceptor && interceptor.candidateToIgnoreId &&
                            <Col sm={24} md={5}>
                                <FormItem label=" " colon={false}>
                                    <Button className={this.isIgnore() ? 'button-ignored': 'button-not-ignored'} onClick={() => this.handleOnChangeCandidateToIgnore(!this.isIgnore())}>{i18n.t(this.isIgnore() ? 'ignored' : 'ignore')}</Button>
                                </FormItem>
                            </Col>
                        }
                        <Col sm={24} md={24} >
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: interceptor && interceptor.description
                                    })(<Input disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]) && status)}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('life_cycle')}>
                                {
                                    getFieldDecorator('lifeCycle', {
                                        initialValue: interceptor ? interceptor.lifeCycle : lifeCycleInitial,
                                        rules: [
                                            { required: true, message: i18n.t('please_select_life_cycle') }
                                        ]
                                    })(<Select onChange={this.handleLifeCycle} disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]) && status)}>
                                        {apiId && <Select.Option value="API">{i18n.t('api')}</Select.Option>}
                                        {planId && <Select.Option value="PLAN">{i18n.t('plan')}</Select.Option>}
                                        {resourceId && <Select.Option value="RESOURCE">{i18n.t('resource')}</Select.Option>}
                                        {operationId && <Select.Option value="OPERATION">{i18n.t('operation')}</Select.Option>}
                                    </Select>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24} >
                            <FormItem label={i18n.t('content')}>
                                {
                                    getFieldDecorator('content', {
                                        initialValue: interceptor ? interceptor.content : this.formatContent(type),
                                        rules: [
                                            { required: true, message: i18n.t('please_input_content') }
                                        ]
                                    })(<TextArea rows={8} required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]) && status)}/>)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>
            </Row >
        )
    }
}

InterceptorForm.propTypes = {
    fetching: PropTypes.bool,
    loading: PropTypes.bool,
    plans: PropTypes.array.isRequired,
    order: PropTypes.number,
}

InterceptorForm.defaultProps = {
    fetching: false,
    loading: false,
    plans: []
}

export default Form.create({})(InterceptorForm)