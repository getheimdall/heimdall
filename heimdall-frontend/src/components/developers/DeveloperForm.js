import React, { Component } from 'react'
import PropTypes from 'prop-types'

import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal } from 'antd'

const FormItem = Form.Item
const confirm = Modal.confirm;

class DeveloperForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (developerId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(developerId)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form

        const { developer } = this.props
        const { loading } = this.props

        return (
            <Row>
                <Form>
                    {developer && getFieldDecorator('id', { initialValue: developer.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={24} >
                            <FormItem label="Name">
                                {
                                    getFieldDecorator('name', {
                                        initialValue: developer && developer.name,
                                        rules: [{ required: true, message: 'Please input your name!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label="Email">
                                {
                                    getFieldDecorator('email', {
                                        initialValue: developer && developer.email,
                                        rules: [{ required: true, type: 'email', message: 'Please input a valid email!' }]
                                    })(<Input required />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={5}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: developer ? developer.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <Tooltip title="Delete">
                        <Button className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!developer} onClick={developer && this.showDeleteConfirm(developer.id)} loading={loading} />
                    </Tooltip>
                    <Tooltip title="Save">
                        <Button className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} />
                    </Tooltip>
                </Row>
            </Row>
        )
    }
}

DeveloperForm.propTypes = {
    loading: PropTypes.bool
}

DeveloperForm.defaultProps = {
    loading: false
}

export default Form.create({})(DeveloperForm)