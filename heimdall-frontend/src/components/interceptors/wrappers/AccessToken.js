import React from 'react'
import PropType from 'prop-types'
import { Form, Input, Col } from 'antd'

import i18n from "../../../i18n/i18n"
import {PrivilegeUtils} from '../../../utils/PrivilegeUtils'
import {privileges} from '../../../constants/privileges-types'

const FormItem = Form.Item

class AccessToken extends React.Component {

    render() {
        const { content } = this.props
        const { getFieldDecorator } = this.props.form

        console.log(content)
        return(
            <React.Fragment>
                {
                    getFieldDecorator('content.location', {
                        initialValue: 'HEADER',
                    })(<Input type="hidden"/>)
                }
                <Col sm={4} md={24}>
                    <FormItem label={i18n.t('access_token_name')}>
                        {
                            getFieldDecorator('content.name', {
                                initialValue: content && content.name ? content.name : 'access_token',
                                rules:[
                                    { required: true, message: i18n.t('please_input_access_token_name') }
                                ]
                            })(<Input required disabled={!(PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_UPDATE_INTERCEPTOR, privileges.PRIVILEGE_CREATE_INTERCEPTOR]))} />)
                        }
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}

AccessToken.defaultProps = {
    form: PropType.object.required
}

export default AccessToken
