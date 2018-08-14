import React, { Component } from 'react'
import PropTypes from 'prop-types'

import { Row, Form, Input, InputNumber, Col, Select } from 'antd'
import { getTemplate } from '../../utils/InterceptorUtils'
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";
import {privileges} from "../../constants/privileges-types";

const FormItem = Form.Item
const { TextArea } = Input

class InterceptorForm extends Component {

    componentDidMount() {
        this.props.onRef(this)
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
        if (value === 'PLAN') {
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

    render() {
        const { getFieldDecorator } = this.props.form

        const {
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


        return (
            <Row>
                <Form>
                    {interceptor && getFieldDecorator('id', { initialValue: interceptor.id })(<Input type='hidden' />)}
                    {interceptor && interceptor.uuid && getFieldDecorator('uuid', { initialValue: interceptor.uuid })(<Input type='hidden' />)}
                    {getFieldDecorator('referenceId', { initialValue: interceptor ? interceptor.referenceId : referenceId })(<Input type='hidden' />)}
                    {environmentId && getFieldDecorator('environment', { initialValue: environmentId })(<Input type='hidden' />)}

                    <Row gutter={24}>
                        <Col sm={24} md={12} >
                            <FormItem label="Type">
                                {
                                    getFieldDecorator('type', {
                                        initialValue: type,
                                    })(<Select disabled>
                                        <Select.Option value="LOG">LOG</Select.Option>
                                        <Select.Option value="MOCK">MOCK</Select.Option>
                                        <Select.Option value="RATTING">RATTING</Select.Option>
                                        <Select.Option value="ACCESS_TOKEN">ACCESS TOKEN</Select.Option>
                                        <Select.Option value="CLIENT_ID">CLIENT ID</Select.Option>
                                        <Select.Option value="CUSTOM">CUSTOM</Select.Option>
                                    </Select>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label="Execution Point">
                                {
                                    getFieldDecorator('executionPoint', {
                                        initialValue: executionPoint,
                                    })(<Select disabled>
                                        <Select.Option value="FIRST">PRE</Select.Option>
                                        <Select.Option value="SECOND">POST</Select.Option>
                                    </Select>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24} >
                            <FormItem label="Name">
                                {
                                    getFieldDecorator('name', {
                                        initialValue: interceptor && interceptor.name,
                                        rules: [
                                            { required: true, message: 'Please define the name!' }
                                        ]
                                    })(<Input required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24} >
                            <FormItem label="Description">
                                {
                                    getFieldDecorator('description', {
                                        initialValue: interceptor && interceptor.description
                                    })(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={18}>
                            <FormItem label="Life Cycle">
                                {
                                    getFieldDecorator('lifeCycle', {
                                        initialValue: interceptor ? interceptor.lifeCycle : lifeCycleInitial,
                                        rules: [
                                            { required: true, message: 'Please select the life cycle!' }
                                        ]
                                    })(<Select onChange={this.handleLifeCycle} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}>
                                        {planId && <Select.Option value="PLAN">PLAN</Select.Option>}
                                        {resourceId && <Select.Option value="RESOURCE">RESOURCE</Select.Option>}
                                        {operationId && <Select.Option value="OPERATION">OPERATION</Select.Option>}
                                    </Select>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={6} >
                            <FormItem label="Order">
                                {
                                    getFieldDecorator('order', {
                                        initialValue: interceptor ? interceptor.order : 0
                                    })(<InputNumber min={0} max={99} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>)
                                }
                            </FormItem>
                        </Col>
                        {type !== 'LOG' && <Col sm={24} md={24} >
                            <FormItem label="Content">
                                {
                                    getFieldDecorator('content', {
                                        initialValue: interceptor ? interceptor.content : this.formatContent(type),
                                        rules: [
                                            { required: true, message: 'Please select the execution point!' }
                                        ]
                                    })(<TextArea rows={6} required disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR])}/>)
                                }
                            </FormItem>
                        </Col>}
                    </Row>
                </Form>
            </Row >
        )
    }
}

InterceptorForm.propTypes = {
    fetching: PropTypes.bool,
    loading: PropTypes.bool,
    plans: PropTypes.array.isRequired
}

InterceptorForm.defaultProps = {
    fetching: false,
    loading: false,
    plans: []
}

export default Form.create({})(InterceptorForm)