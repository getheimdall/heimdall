import React, { Component } from 'react'
import PropTypes from 'prop-types'

import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal } from 'antd'

const FormItem = Form.Item
const confirm = Modal.confirm;

class EnvironmentForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (environmentId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(environmentId)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form

        const { environment } = this.props
        const { loading } = this.props

        return (
            <Row>
                <Form>
                    {environment && getFieldDecorator('id', { initialValue: environment.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={12} >
                            <FormItem label="Name">
                                {
                                    getFieldDecorator('name', {
                                        initialValue: environment && environment.name,
                                        rules: [{ required: true, message: 'Please input your environment name!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Inbound URL">
                                {
                                    getFieldDecorator('inboundURL', {
                                        initialValue: environment && environment.inboundURL,
                                        rules: [{ required: true, message: 'Please input your environment inbound!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Description">
                                {
                                    getFieldDecorator('description', {
                                        initialValue: environment && environment.description,
                                        rules: [{ required: true, message: 'Please input your environment description!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>

                        <Col sm={24} md={12}>
                            <FormItem label="Outbound URL">
                                {
                                    getFieldDecorator('outboundURL', {
                                        initialValue: environment && environment.outboundURL,
                                        rules: [{ required: true, message: 'Please input your environment outbound!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>

                        <Col sm={24} md={5}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: environment ? environment.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <Tooltip title="Delete">
                        <Button className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!environment} onClick={environment && this.showDeleteConfirm(environment.id)} loading={loading} />
                    </Tooltip>
                    <Tooltip title="Save">
                        <Button className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                    </Tooltip>
                </Row>
            </Row>
        )
    }
}

EnvironmentForm.propTypes = {
    loading: PropTypes.bool
}

EnvironmentForm.defaultProps = {
    loading: false
}

export default Form.create({})(EnvironmentForm)