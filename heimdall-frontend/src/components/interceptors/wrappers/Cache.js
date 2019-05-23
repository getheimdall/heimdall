import React from 'react'
import { Form, Input, Col } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item
const TextArea = Input.TextArea

class Cache extends React.Component {

    render() {
        const { content } = this.props
        const { getFieldDecorator } = this.props.form

        return(
            <React.Fragment>
                <Col sm={4} md={12}>
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
                <Col sm={4} md={12}>
                    <FormItem label={i18n.t('time_to_live')}>
                        {
                            getFieldDecorator('content.timeToLive', {
                                initialValue: content ? content.timeToLive : 10000,
                                rules:[
                                    { required: true, message: i18n.t('please_input_time_to_live') }
                                ]
                            })(<Input type="number" required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
                <Col sm={4} md={12}>
                    <FormItem label={i18n.t('headers')}>
                        {
                            getFieldDecorator('content.headers', {
                                initialValue: content ? content.headers : 'header1, header2'
                            })(<TextArea required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
                <Col sm={4} md={12}>
                    <FormItem label={i18n.t('query_params')}>
                        {
                            getFieldDecorator('content.queryParams', {
                                initialValue: content ? content.queryParams : 'queryParam1, queryParam2'
                            })(<TextArea required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}


export default Cache
