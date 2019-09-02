import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Row, Form, Input, Col, Switch, Tooltip, Button, Modal, Select, Spin } from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"
import {privileges} from "../../constants/privileges-types"

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
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
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
                    {user && getFieldDecorator('password', { initialValue: user.password })(<Input type='hidden' />)}
                    <Row gutter={24}>
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('username')}>
                                {
                                    getFieldDecorator('userName', {
                                        initialValue: user && user.userName,
                                        rules: [
                                            { required: true, message: i18n.t('please_input_username') },
                                            { min: 5, message: i18n.t('min_5_characters_to_username') }
                                        ]
                                    })(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])} />)
                                }
                            </FormItem>
                        </Col>
                        {!user &&
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('password')}>
                                {
                                    getFieldDecorator('password', {
                                        initialValue: user && user.password,
                                        rules: [
                                            { required: true, message: i18n.t('please_input_password') },
                                            { min: 5, message: i18n.t('min_5_characters_to_password') }
                                        ]
                                    })(<Input type="password" disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])} />)
                                }
                            </FormItem>
                        </Col>
                        }
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('first_name')}>
                                {
                                    getFieldDecorator('firstName', {
                                        initialValue: user && user.firstName,
                                        rules: [
                                            { required: true, message: i18n.t('please_input_first_name') },
                                            { min: 3, message: i18n.t('min_3_characters_to_first_name') }
                                        ]
                                    })(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('last_name')}>
                                {
                                    getFieldDecorator('lastName', {
                                        initialValue: user && user.lastName,
                                        rules: [
                                            { required: true, message: i18n.t('please_input_last_name') },
                                            { min: 3, message: i18n.t('min_3_characters_to_last_name') }
                                        ]
                                    })(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12} >
                            <FormItem label={i18n.t('email')}>
                                {
                                    getFieldDecorator('email', {
                                        initialValue: user && user.email,
                                        rules: [
                                            { required: true, type: 'email', message: i18n.t('please_input_valid_email') }
                                        ]
                                    })(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('roles')}>
                                {!roles ? <Spin /> :
                                    getFieldDecorator('roles', {
                                        initialValue: user && user.roles.map(role => role.id),
                                        rules: [
                                            { required: true, message: i18n.t('please_select_role') },
                                        ]
                                    })(
                                        <Select optionFilterProp="children" mode="multiple" style={{ width: '100%' }} placeholder={i18n.t('please_select_role')} disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER]) || !roles}>
                                            {roles && roles.map(role => <Select.Option key={role.id} value={role.id}>{role.name}</Select.Option>)}
                                        </Select>
                                    )
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={5}>
                            <FormItem label={i18n.t('status')}>
                                {
                                    getFieldDecorator('status', {
                                        initialValue: user ? user.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER])}  />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_USER]}>
                        <Tooltip title={i18n.t('delete')}>
                            <Button id="deleteUser" className="card-button" type="danger" ghost icon="delete" size="large" shape="circle" disabled={!user} onClick={user && this.showDeleteConfirm(user.id)} loading={loading} />
                        </Tooltip>
                    </ComponentAuthority>
                    <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_USER, privileges.PRIVILEGE_UPDATE_USER]}>
                        <Tooltip title={i18n.t('save')}>
                            <Button id="saveUser" className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} loading={loading} disabled={!roles} />
                        </Tooltip>
                    </ComponentAuthority>
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