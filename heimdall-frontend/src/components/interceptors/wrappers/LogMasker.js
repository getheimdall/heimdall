import React from 'react'
import PropType from 'prop-types'
import { Form, Input, Col, Switch, Row } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item

class LogMasker extends React.Component {

    state = {
        header: true
    }

    toggleHeader = () => {
        console.log('toggleHeader')
        this.setState({ ...this.state, header: !this.state.header })
    }

    render() {

        const { getFieldDecorator } = this.props.form

        return(
            <React.Fragment>
                <Col sm={24} md={24}>
                    <Row type="flex" justify="space-around" align="middle">
                        <Col sm={4} md={8}>
                            <FormItem label={i18n.t('body')}>
                                {
                                    getFieldDecorator('content.body', {
                                        initialValue: true,
                                        rules:[
                                            { required: true, message: i18n.t('please_input_type_oauth') }
                                        ]
                                    })(<Switch required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} defaultChecked/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={4} md={8}>
                            <FormItem label={i18n.t('uri')}>
                                {
                                    getFieldDecorator('content.uri', {
                                        initialValue: true,
                                        rules:[
                                            { required: true, message: i18n.t('please_input_type_oauth') }
                                        ]
                                    })(<Switch disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} defaultChecked/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={4} md={8}>
                            <FormItem label={i18n.t('headers')}>
                                {
                                    getFieldDecorator('content.headers', {
                                        initialValue: true,
                                        rules:[
                                            { required: true, message: i18n.t('please_input_type_oauth') }
                                        ]
                                    })(<Switch onChange={this.toggleHeader} disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} defaultChecked/>)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Col>
                {
                    this.state.header &&
                    <Col sm={4} md={24}>
                        <FormItem label={i18n.t('ignored_headers')}>
                            {
                                getFieldDecorator('content.ignoredHeaders', {
                                    initialValue: 'someHeader, anotherHeader',
                                })(<Input.TextArea disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                            }
                        </FormItem>
                    </Col>
                }
            </React.Fragment>
        )
    }
}

LogMasker.defaultProps = {
    form: PropType.object.required
}

export default LogMasker
