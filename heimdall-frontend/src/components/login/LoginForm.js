import React, { Component } from 'react'
import { Row, Col, Form, Icon, Input, Button } from 'antd';
import i18n from '../../i18n/i18n'
import {ReCaptcha} from "react-recaptcha-google";

const configJs = window._env_
const siteKey = `${configJs.RECAPTCHA_SITE_KEY}`
const captchaEnabled = `${configJs.RECAPTCHA_ENABLED}`

const FormItem = Form.Item;

class LoginForm extends Component {


    constructor(props, context) {
        super(props, context);
        this.captchaResponse = captchaEnabled;
        this.onLoadRecaptcha = this.onLoadRecaptcha.bind(this);
        this.verifyCallback = this.verifyCallback.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount() {
        this.onLoadRecaptcha();
    }

    onLoadRecaptcha() {
        if (this.captcha && (captchaEnabled === 'true')) {
            this.captcha.reset(this.captcha);
            this.captcha.execute(this.captcha);
        }
    }
    verifyCallback(response) {
       if (captchaEnabled === 'true') {
           if (response) {
               this.captchaResponse = response;
           }else{
               this.captchaResponse = captchaEnabled;
           }
        }else{
            this.captchaResponse = captchaEnabled;
        }
    }

    handleSubmit = (e) => {
        if(this.captcha && (captchaEnabled === 'true')) {
            this.captcha.execute();
        }
        e.preventDefault();
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.submit(values.userName, values.password, true, this.captchaResponse)
            }
        });
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        let recaptcha
        if (captchaEnabled === 'true') {
            recaptcha =
               <ReCaptcha
                ref={(el) => {this.captcha = el;}}
                size="invisible"
                render="explicit"
                sitekey={siteKey}
                onloadCallback={this.onLoadRecaptcha}
                verifyCallback={this.verifyCallback}
            />
        }else{
            recaptcha = null;
        }
        return (
            <Form onSubmit={this.handleSubmit}>
                <Row>
                    <Col>
                        <FormItem>
                            {getFieldDecorator('userName', {
                                rules: [{ required: true, message: i18n.t('insert_username') }],
                            })(
                                <Input prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder={i18n.t('username')} autoFocus/>
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
                {recaptcha}
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