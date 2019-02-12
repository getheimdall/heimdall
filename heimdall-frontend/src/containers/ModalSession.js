import React, { Component } from 'react'
import PropTypes from "prop-types"
import { connect } from 'react-redux'
import {Button, Col, Form, Icon, Input, Modal, Row, Skeleton} from 'antd'

import i18n from "../i18n/i18n"
import {login} from "../actions/auth"
import Session from "../utils/SessionManagement"
import {clearTime, closeModalSession} from "../actions/session"

const FormItem = Form.Item;

class ModalSession extends Component {

    componentDidMount() {
        Session.addDispatch('modalSession', this.props.dispatch)
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.time && nextProps.time !== 0) {
            const timeExpire = nextProps.time * 1000
            this.props.dispatch(clearTime())
            Session.createTimeOut(timeExpire)
        }
    }

    confirmSession = () => {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.dispatch(login(values.userName, values.password, false))
                this.props.form.resetFields()
            }
        });
    }

    cancelSession = () => {
        Session.cancelTimeouts()
        this.props.dispatch(closeModalSession())
        this.props.history.push('/login')
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        return (
            <Modal centered maskClosable={false} title={i18n.t('session_expired')} visible={this.props.visible} onOk={this.confirmSession} onCancel={this.cancelSession} footer={[
                <Button key="back" onClick={this.cancelSession} htmlType="button">{i18n.t('cancel')}</Button>,
                <Button id="login" key="login" onClick={this.confirmSession} htmlType="submit" type="primary" loading={this.props.loading}>{i18n.t('sign_in')}</Button>
            ]}>
                <div>
                        <Form>
                            <Row>
                                <Skeleton loading={this.props.loading} active paragraph={{ rows: 0 }} style={{ width: '100%' }}>
                                    <Col>
                                        <FormItem>
                                            {getFieldDecorator('userName', {
                                                rules: [{ required: true, message: i18n.t('insert_username') }],
                                            })(
                                                <Input prefix={<Icon type="user" style={{ color: 'rgba(0,0,0,.25)' }} />} placeholder={i18n.t('username')} autoFocus/>
                                            )}
                                        </FormItem>
                                    </Col>
                                </Skeleton>
                                <Skeleton loading={this.props.loading} active paragraph={{ rows: 0 }} style={{ width: '100%' }}>
                                    <Col>
                                        <FormItem>
                                            {getFieldDecorator('password', {
                                                rules: [{ required: true, message: i18n.t('insert_password') }],
                                            })(
                                                <Input prefix={<Icon type="lock" style={{ color: 'rgba(0,0,0,.25)' }} />} type="password" placeholder={i18n.t('password')} />
                                            )}
                                        </FormItem>
                                    </Col>
                                </Skeleton>
                            </Row>
                        </Form>
                </div>
            </Modal>
        )
    }
}

ModalSession.defaultProps = {
    visible: false
}

ModalSession.propTypes = {
    history: PropTypes.object.isRequired
}

const WrappedModalSession = Form.create()(ModalSession);

const mapStateToProps = state => {
    return {
        visible: state.session.visible,
        time: state.session.time,
        loading: state.auth.loading
    }
}

export default connect(mapStateToProps)(WrappedModalSession)