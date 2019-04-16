import React from 'react'
import { Form, Input, Col } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item
const TextArea = Input.TextArea

class Default extends React.Component {

    render() {

        const { getFieldDecorator } = this.props.form

        return(
            <Col sm={4} md={24}>
                <FormItem label={i18n.t('content')}>
                    {
                        getFieldDecorator('content', {
                            initialValue: '',
                            rules:[
                                { required: true, message: i18n.t('please_input_content') }
                            ]
                        })(<TextArea rows={5} disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))}/>)
                    }
                </FormItem>
            </Col>
        )
    }
}

export default Default
