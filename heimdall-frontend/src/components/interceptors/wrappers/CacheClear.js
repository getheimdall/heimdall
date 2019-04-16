import React from 'react'
import { Form, Input, Col } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item

class Cache extends React.Component {

    render() {
        const { content } = this.props
        const { getFieldDecorator } = this.props.form

        return(
            <React.Fragment>
                <Col sm={4} md={24}>
                    <FormItem label={i18n.t('cache_name')}>
                        {
                            getFieldDecorator('content.cache', {
                                initialValue: content ? content.cache : 'cache-name',
                                rules:[
                                    { required: true, message: i18n.t('please_input_cache_name') }
                                ]
                            })(<Input required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}


export default Cache
