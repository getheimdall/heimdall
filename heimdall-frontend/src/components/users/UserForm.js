import React, { Component } from 'react'
import PropTypes from 'prop-types'

import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal, Select, Spin } from 'antd'

const FormItem = Form.Item
const confirm = Modal.confirm

class UserForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                if (payload.roles) {
                    let roles = payload.roles;
                    payload.roles = roles.map((role) => ({ id: role }))
                }

                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (userId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(userId)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const { user, loading, roles } = this.props

        return (
            <Row>
                <Form>
                    {user && getFieldDecorator('id', { initialValue: user.id })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={12} >
                            <FormItem label="Username">
                                {
                                    getFieldDecorator('userName', {
                                        initialValue: user && user.userName,
                                        rules: [
                                            { required: true, message: 'Please input an username!' },
                                            { min: 5, message: 'Min of 5 characters to username!' }
                                        ]
                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label="Password">
                                {
                                    getFieldDecorator('password', {
                                        initialValue: user && user.password,
                                        rules: [
                                            { required: true, message: 'Please input a password!' },
                                            { min: 5, message: 'Min of 5 characters to the password!' }
                                        ]
                                    })(<Input type="password" />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label="First Name">
                                {
                                    getFieldDecorator('firstName', {
                                        initialValue: user && user.firstName,
                                        rules: [
                                            { required: true, message: 'Please input a first name!' },
                                            { min: 3, message: 'Min of 3 characters to first name!' }
                                        ]
                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label="Last Name">
                                {
                                    getFieldDecorator('lastName', {
                                        initialValue: user && user.lastName,
                                        rules: [
                                            { required: true, message: 'Please input a last name!' },
                                            { min: 3, message: 'Min of 3 characters to last name!' }
                                        ]
                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label="Email">
                                {
                                    getFieldDecorator('email', {
                                        initialValue: user && user.email,
                                        rules: [
                                            { required: true, type: 'email', message: 'Please input a valid email!' }
                                        ]
                                    })(<Input />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Roles">
                                {!roles ? <Spin /> :
                                    getFieldDecorator('roles', {
                                        initialValue: user && user.roles.map(role => role.id),
                                        rules: [
                                            { required: true, message: 'Please select a role!' },
                                        ]
                                    })(
                                        <Select optionFilterProp="children" mode="multiple" style={{ width: '100%' }} placeholder="Please select a role" disabled={!roles}>
                                            {roles && roles.map(role => <Select.Option key={role.id} value={role.id}>{role.name}</Select.Option>)}
                                        </Select>
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={5}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: user ? user.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <Tooltip title="Delete">
                        <Button className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!user} onClick={user && this.showDeleteConfirm(user.id)} loading={loading} />
                    </Tooltip>
                    <Tooltip title="Save">
                        <Button className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} disabled={!roles} />
                    </Tooltip>
                </Row>
            </Row >
        )
    }
}

UserForm.propTypes = {
    loading: PropTypes.bool
}

UserForm.defaultProps = {
    loading: false
}

export default Form.create({})(UserForm)