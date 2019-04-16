import React from 'react'
import { Form, Input, Col, Select } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item

class Ratting extends React.Component {

    render() {
        const { content } = this.props
        const { getFieldDecorator } = this.props.form

        return(
            <React.Fragment>
                <Col sm={4} md={24}>
                    <FormItem label={i18n.t('calls')}>
                        {
                            getFieldDecorator('content.calls', {
                                initialValue: content && content.calls ? content.calls : 20,
                                rules:[
                                    { required: true, message: i18n.t('please_input_calls') }
                                ]
                            })(<Input type="number" required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))}/>)
                        }
                    </FormItem>
                </Col>
                <Col sm={4} md={24}>
                    <FormItem label={i18n.t('interval')}>
                        {
                            getFieldDecorator('content.interval', {
                                initialValue: content && content.interval ? content.interval : 'MINUTES',
                                rules:[
                                    { required: true, message: i18n.t('please_input_interval') }
                                ]
                            })(
                                <Select required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))}>
                                    <Select.Option key={"SECONDS"} value="SECONDS">{i18n.t('seconds')}</Select.Option>
                                    <Select.Option key={"MINUTES"} value="MINUTES">{i18n.t('minutes')}</Select.Option>
                                    <Select.Option key={"HOURS"} value="HOURS">{i18n.t('hours')}</Select.Option>
                                </Select>
                            )
                        }
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}

export default Ratting
