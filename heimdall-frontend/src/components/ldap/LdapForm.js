import React, {Component} from 'react'

import {Button, Col, Form, Input, Row, Switch, Tooltip} from 'antd'
import PropTypes from "prop-types";
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";
import {privileges} from "../../constants/privileges-types";
import ComponentAuthority from "../policy/ComponentAuthority";

const FormItem = Form.Item

class LdapForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                this.props.handleSubmit(payload)
            }
        });
    }

    render() {

        const {getFieldDecorator} = this.props.form
        const {ldap, loading} = this.props

        return (
            <Row>
                <Form>
                    {ldap && getFieldDecorator('id', {initialValue: ldap.id})(<Input type='hidden'/>)}

                    <Row gutter={24}>
                        <Col sm={24} md={12}>
                            <FormItem label="URL">
                                {
                                    getFieldDecorator('url', {
                                        initialValue: ldap ? ldap.url : '',
                                        rules: [
                                            { required: true,  message: 'Please input the url!' },
                                            { max: 200, message: 'Max of the 200 characters to url!' }
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_LDAP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Search base">
                                {
                                    getFieldDecorator('searchBase', {
                                        initialValue: ldap ? ldap.searchBase : '',
                                        rules: [
                                            { required: true,  message: 'Please input the search base!' }
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_LDAP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="UserDn">
                                {
                                    getFieldDecorator('userDn', {
                                        initialValue: ldap ? ldap.userDn : '',
                                        rules: [
                                            { required: true,  message: 'Please input the userDn!' },
                                            { max: 100, message: 'Max of the 100 characters to url!' }
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_LDAP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label="Password">
                                {
                                    getFieldDecorator('password', {
                                        initialValue: ldap ? ldap.password : '',
                                        rules: [
                                            { required: true,  message: 'Please input the password!' }
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_LDAP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label="User search filter">
                                {
                                    getFieldDecorator('userSearchFilter', {
                                        initialValue: ldap ? ldap.userSearchFilter : '',
                                        rules: [
                                            { required: true,  message: 'Please input the user search filter!' },
                                            { max: 200, message: 'Max of the 200 characters to url!' }
                                        ]
                                    })(<Input required
                                              disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_LDAP])}/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={5}>
                            <FormItem label="Status">
                                {
                                    getFieldDecorator('status', {
                                        initialValue: ldap ? ldap.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required
                                               disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_LDAP])}/>)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <ComponentAuthority
                        privilegesAllowed={[privileges.PRIVILEGE_CREATE_PLAN, privileges.PRIVILEGE_UPDATE_PLAN]}>
                        <Tooltip title="Save">
                            <Button className="card-button" type="primary" icon="save" size="large" shape="circle"
                                    onClick={this.onSubmitForm} loading={loading}/>
                        </Tooltip>
                    </ComponentAuthority>
                </Row>
            </Row>
        )
    }
}

LdapForm.propTypes = {
    loading: PropTypes.bool,
    ldap: PropTypes.object
}

export default Form.create({})(LdapForm)