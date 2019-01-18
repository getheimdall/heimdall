import React, { Component } from 'react'
import { Row, Col, Form, Icon, Input, Button } from 'antd';
import i18n from '../../i18n/i18n'

const FormItem = Form.Item;

class LoginForm extends Component {

    handleSubmit = (e) => {
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.submit(values.userName, values.password)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        return (
            <Form onSubmit={this.handleSubmit}>
                <Row>
                    <Col>
                        <FormItem>
                            {getFieldDecorator('userName', {
                                rules: [{ required: true, message: i18n.t('insert_username') }],
                            })(
                                <Input prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder={i18n.t('username')} />
                            )}
                        </FormItem>
                    </Col>
                </Row>
                <FormItem>
                    {getFieldDecorator('password', {
                        rules: [{ required: true, message: i18n.t('insert_password') }],
                    })(
                        <Input prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder={i18n.t('password')} />
                    )}
                </FormItem>
                {/* <Row >
                    <Col>
                        <FormItem>
                            {getFieldDecorator('remember', {
                                valuePropName: 'checked',
                                initialValue: true,
                            })(
                                <Checkbox>Remember me</Checkbox>
                            )}
                            <a style={{ float: 'right' }} className="login-form-forgot" href="">Forgot password</a>
                        </FormItem>
                    </Col>
                </Row> */}
                <Col>
                    <Button id="login" style={{ width: '100%', background: 'transparent', border: '1px solid #D9B217' }} type="primary" htmlType="submit" className="login-form-button" loading={this.props.loading}>
                        { i18n.t('sign_in') }
                    </Button>
                </Col>
            </Form>
        );
    }
}

const WrappedLoginForm = Form.create()(LoginForm);

export default WrappedLoginForm