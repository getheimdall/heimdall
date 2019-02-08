import React, {Component} from 'react'
import {Button, Col, Form, Input, Row, Tooltip} from 'antd'

import i18n from "../../i18n/i18n"
import {PrivilegeUtils} from "../../utils/PrivilegeUtils"

const FormItem = Form.Item

class UserEditPasswordForm extends Component {

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
               this.props.handleSubmit(payload)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form

        return (
            <Row>
                <Form>
                    <Row gutter={24} className={'flex'}>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('current_password')}>
                                {
                                    getFieldDecorator('current_password', {
                                        initialValue: '',
                                        rules: [
                                            { required: true, message: i18n.t('please_input_current_password') },
                                            { min: 5, message: i18n.t('min_5_characters_to_current_password')},
                                            { max: 16, message: i18n.t('max_16_characters_to_current_password') }
                                        ]
                                    })(<Input type="password" disabled={PrivilegeUtils.verifyTypeUser('LDAP')} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('new_password')}>
                                {
                                    getFieldDecorator('new_password', {
                                        initialValue: '',
                                        rules: [
                                            { required: true, message: i18n.t('please_input_new_password') },
                                            { min: 5, message: i18n.t('min_5_characters_to_new_password')},
                                            { max: 16, message: i18n.t('max_16_characters_to_new_password') }
                                        ]
                                    })(<Input type="password" disabled={PrivilegeUtils.verifyTypeUser('LDAP')} />)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('confirm_new_password')}>
                                {
                                    getFieldDecorator('confirm_new_password', {
                                        initialValue: '',
                                        rules: [
                                            { required: true, message: i18n.t('please_input_confirm_new_password') },
                                            { min: 5, message: i18n.t('min_5_characters_to_confirm_new_password')},
                                            { max: 16, message: i18n.t('max_16_characters_to_confirm_new_password') }
                                        ]
                                    })(<Input type="password" disabled={PrivilegeUtils.verifyTypeUser('LDAP')} />)
                                }
                            </FormItem>
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    {
                        PrivilegeUtils.verifyTypeUser('DATABASE') &&
                        <Tooltip title={i18n.t('change_password')}>
                            <Button id="change_password" className="card-button" type="primary" icon="save" size="large" shape="circle" onClick={this.onSubmitForm} />
                        </Tooltip>
                    }
                </Row>
            </Row >
        )
    }
}


export default Form.create({})(UserEditPasswordForm)