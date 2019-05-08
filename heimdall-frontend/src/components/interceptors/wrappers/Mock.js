import React from 'react'
import { Form, Input, Col,  } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item
const { TextArea } = Input

class Mock extends React.Component {

    render() {
        const { content } = this.props
        const { getFieldDecorator } = this.props.form

        return(
            <React.Fragment>
                <Col sm={4} md={24}>
                    <FormItem label={i18n.t('body')}>
                        {
                            getFieldDecorator('content.body', {
                                initialValue: content ? content.body : "{'name': 'Mock Example'}",
                                rules:[
                                    { required: true, message: i18n.t('please_input_body') }
                                ]
                            })(<TextArea rows={6} required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))}/>)
                        }
                    </FormItem>
                </Col>
                <Col sm={4} md={24}>
                    <FormItem label={i18n.t('status')}>
                        {
                            getFieldDecorator('content.status', {
                                initialValue: content ? content.status : 200,
                                rules:[
                                    { required: true, message: i18n.t('please_input_status') }
                                ]
                            })(<Input type="number" required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}

export default Mock
