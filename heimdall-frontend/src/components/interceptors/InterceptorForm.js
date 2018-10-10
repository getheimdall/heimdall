import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Form, Input, InputNumber, Col, Select } from 'antd'

import i18n from "../../i18n/i18n"
import { getTemplate } from '../../utils/InterceptorUtils'

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

    render() {
        const { getFieldDecorator } = this.props.form
        
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


        return (
            <Row>
                <Form>
                    {interceptor && getFieldDecorator('id', { initialValue: interceptor.id })(<Input type='hidden' />)}
                    {interceptor && interceptor.uuid && getFieldDecorator('uuid', { initialValue: interceptor.uuid })(<Input type='hidden' />)}
                    {getFieldDecorator('referenceId', { initialValue: interceptor ? interceptor.referenceId : referenceId })(<Input type='hidden' />)}
                    {environmentId && getFieldDecorator('environment', { initialValue: environmentId })(<Input type='hidden' />)}

                    <Row gutter={24}>
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('type')}>
                                {
                                    getFieldDecorator('type', {
                                        initialValue: type,
                                    })(<Select disabled>
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
                            <FormItem label={i18n.t('execution_point')}>
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
                            <FormItem label={i18n.t('name')}>
                                {
                                    getFieldDecorator('name', {
                                        initialValue: interceptor && interceptor.name,
                                        rules: [
                                            { required: true, message: i18n.t('please_define_name') }
                                        ]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24} >
                            <FormItem label={i18n.t('description')}>
                                {
                                    getFieldDecorator('description', {
                                        initialValue: interceptor && interceptor.description
                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={18}>
                            <FormItem label={i18n.t('life_cycle')}>
                                {
                                    getFieldDecorator('lifeCycle', {
                                        initialValue: interceptor ? interceptor.lifeCycle : lifeCycleInitial,
                                        rules: [
                                            { required: true, message: i18n.t('please_select_life_cycle') }
                                        ]
                                    })(<Select onChange={this.handleLifeCycle}>
                                        {apiId && <Select.Option value="API">{i18n.t('api')}</Select.Option>}
                                        {planId && <Select.Option value="PLAN">{i18n.t('plan')}</Select.Option>}
                                        {resourceId && <Select.Option value="RESOURCE">{i18n.t('resource')}</Select.Option>}
                                        {operationId && <Select.Option value="OPERATION">{i18n.t('operation')}</Select.Option>}
                                    </Select>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={6} >
                            <FormItem label={i18n.t('order')}>
                                {
                                    getFieldDecorator('order', {
                                        initialValue: interceptor && interceptor.order ? interceptor.order : this.props.order
                                    })(<InputNumber min={0} max={99} disabled/>)
                                }
                            </FormItem>
                        </Col>
                        {type !== 'LOG' && <Col sm={24} md={24} >
                            <FormItem label={i18n.t('content')}>
                                {
                                    getFieldDecorator('content', {
                                        initialValue: interceptor ? interceptor.content : this.formatContent(type),
                                        rules: [
                                            { required: true, message: i18n.t('please_input_content') }
                                        ]
                                    })(<TextArea rows={6} required />)
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
    plans: PropTypes.array.isRequired,
    order: PropTypes.number,
}

InterceptorForm.defaultProps = {
    fetching: false,
    loading: false,
    plans: []
}

export default Form.create({})(InterceptorForm)