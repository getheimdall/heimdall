import React from 'react'
import PropType from 'prop-types'
import { Form, Input, Col, Select } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item

class OAuth extends React.Component {

    state = {
        isAuthorize: true
    }

    handleOnChangeTypeOAuth = value => {

        if (value === 'AUTHORIZE' && !this.state.isAuthorize) {
            this.setState({ ...this.state, isAuthorize: true })
        }

        if (value === 'VALIDATE' && this.state.isAuthorize) {
            this.setState({ ...this.state, isAuthorize: false })
        }
    }

    render() {

        const { getFieldDecorator } = this.props.form

        return(
            <React.Fragment>
                <Col sm={4} md={12}>
                    <FormItem label={i18n.t('type_oauth')}>
                        {
                            getFieldDecorator('content.typeOAuth', {
                                initialValue: 'AUTHORIZE',
                                rules:[
                                    { required: true, message: i18n.t('please_input_type_oauth') }
                                ]
                            })(<Select required onChange={this.handleOnChangeTypeOAuth} disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))}>
                                <Select.Option key="AUTHORIZE" value="AUTHORIZE">{i18n.t('authorize')}</Select.Option>
                                <Select.Option key="VALIDATE" value="VALIDATE">{i18n.t('validate')}</Select.Option>
                            </Select>)
                        }
                    </FormItem>
                </Col>
                <Col sm={4} md={12}>
                    <FormItem label={i18n.t('private_key')}>
                        {
                            getFieldDecorator('content.privateKey', {
                                initialValue: 'privateKey',
                                rules:[
                                    { required: true, message: i18n.t('please_input_private_key') }
                                ]
                            })(<Input required type="text" disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
                {
                    this.state.isAuthorize &&
                    <React.Fragment>
                        <Col sm={4} md={8}>
                            <FormItem label={i18n.t('provider_id')}>
                                {
                                    getFieldDecorator('content.providerId', {
                                        initialValue: 1,
                                    })(<Input type="number" disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={4} md={8}>
                            <FormItem label={i18n.t('time_access_token')}>
                                {
                                    getFieldDecorator('content.timeAccessToken', {
                                        initialValue: 20
                                    })(<Input type="number" required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={4} md={8}>
                            <FormItem label={i18n.t('time_refresh_token')}>
                                {
                                    getFieldDecorator('content.timeRefreshToken', {
                                        initialValue: 20
                                    })(<Input type="number" required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                                }
                            </FormItem>
                        </Col>
                    </React.Fragment>
                }
            </React.Fragment>
        )
    }
}

OAuth.defaultProps = {
    form: PropType.object.required
}

export default OAuth
