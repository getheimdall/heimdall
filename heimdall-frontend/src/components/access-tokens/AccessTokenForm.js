import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Row, Form, Input, Col, Switch, Tooltip, Button, Modal, AutoComplete, Spin, Icon, Checkbox} from 'antd'

import i18n from "../../i18n/i18n"
// import Loading from '../ui/Loading';

const FormItem = Form.Item
const confirm = Modal.confirm
const Option = AutoComplete.Option

class AccessTokenForm extends Component {

    state = {
        plans: []
    }

    componentDidMount() {
        if (this.props.accessToken && this.props.accessToken.app && this.props.accessToken.app.plans) {
            this.setState({...this.state, plans: this.props.accessToken.app.plans})
        }
    }

    onSubmitForm = () => {
        this.props.form.validateFieldsAndScroll((err, payload) => {
            if (!err) {
                payload.status = payload.status ? 'ACTIVE' : 'INACTIVE'
                payload.app.id = Number(payload.app.id)
                if (payload.plans) {
                    const plans = payload.plans;
                    payload.plans = plans.map((planId) => ({id: planId}))
                }
                this.props.handleSubmit(payload)
            }
        });
    }

    showDeleteConfirm = (accessTokenId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(accessTokenId)
            }
        });
    }

    checkApp = (rule, value, callback) => {
        if (this.props.appSource.some(app => app.id === value)) {
            const app = this.props.appSource.filter(app => app.id === value)[0];
            this.setState({...this.state, plans: app.plans})
            callback();
            return
        }
        callback(i18n.t('you_need_select_app'));
    }

    render() {
        const {getFieldDecorator} = this.props.form

        const {accessToken, loading, appSource, fetching} = this.props
        const {plans} = this.state

        const appAutocompleteSource = appSource.map((app, index) => {
            return <Option key={app.id}>{app.name}</Option>
        })
        return (
            <Row>
                <Form>
                    {accessToken && getFieldDecorator('id', {initialValue: accessToken.id})(<Input type='hidden'/>)}
                    <Row gutter={24}>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('token')}>
                                {
                                    getFieldDecorator('code', {
                                        initialValue: accessToken && accessToken.code,
                                        rules: [
                                            {min: 6, message: i18n.t('min_6_characters_to_token')}
                                        ]
                                    })(<Input/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={24}>
                            <FormItem label={i18n.t('app')}>
                                {
                                    getFieldDecorator('app.id', {
                                        initialValue: accessToken && accessToken.app.id.toString(),
                                        validateTrigger: 'onSelect',
                                        rules: [
                                            {
                                                validator: this.checkApp,
                                                transform: (value) => Number(value),
                                                required: true
                                            }
                                        ]
                                    })(
                                        <AutoComplete
                                            notFoundContent={fetching ? < Spin size="small"/> : null}
                                            filterOption={false}
                                            dataSource={appAutocompleteSource}
                                            onSearch={this.props.handleSearch}
                                            optionLabelProp="children">
                                            <Input addonBefore={<Icon type="search"/>} spellCheck={false}/>
                                        </AutoComplete>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            <FormItem label={i18n.t('status')}>
                                {
                                    getFieldDecorator('status', {
                                        initialValue: accessToken ? accessToken.status === 'ACTIVE' : true,
                                        valuePropName: 'checked'
                                    })(<Switch required/>)
                                }
                            </FormItem>
                        </Col>
                        <Col sm={24} md={12}>
                            { plans && plans.length === 0 && <span>You need select a app</span>}
                            { plans && plans.length > 0 &&
                            <FormItem label={i18n.t('plans')}>
                                {
                                    getFieldDecorator('plans', {
                                        initialValue: accessToken && accessToken.plans.map(plan => plan.id)
                                    })(<Checkbox.Group className='checkbox-conductor'>
                                        {plans && plans.map((plan, index) => {
                                            return <Checkbox key={index} value={plan.id}>{plan.name}</Checkbox>
                                        })}
                                    </Checkbox.Group>)
                                }
                            </FormItem>}
                            {/*{ this.props.plans && this.props.plans.content.length === 0 ? <Loading/> :*/}
                                {/*<FormItem label="Plans">*/}
                                    {/*{*/}
                                        {/*getFieldDecorator('plans', {*/}
                                            {/*initialValue: accessToken && accessToken.plans.map(plan => plan.id)*/}
                                        {/*})(<Checkbox.Group className='checkbox-conductor'>*/}
                                            {/*{this.props.plans && this.props.plans.content.map((plan, index) => {*/}
                                                {/*return <Checkbox key={index} value={plan.id}>{plan.name}</Checkbox>*/}
                                            {/*})}*/}
                                        {/*</Checkbox.Group>)*/}
                                    {/*}*/}
                                {/*</FormItem>}*/}
                        </Col>
                    </Row>
                </Form>

                <Row type="flex" justify="end">
                    <Tooltip title={i18n.t('delete')}>
                        <Button className="card-button" type="danger" ghost icon="delete" size="large" shape="circle"
                                disabled={!accessToken} onClick={accessToken && this.showDeleteConfirm(accessToken.id)}
                                loading={loading} id="deleteAccessToken"/>
                    </Tooltip>
                    <Tooltip title={i18n.t('save')}>
                        <Button className="card-button" type="primary" icon="save" size="large" shape="circle"
                                onClick={this.onSubmitForm} loading={loading} id="saveAccessToken"/>
                    </Tooltip>
                </Row>
            </Row>
        )
    }
}

AccessTokenForm.propTypes = {
    fetching: PropTypes.bool,
    loading: PropTypes.bool,
    appSource: PropTypes.array.isRequired,
    plans: PropTypes.object.isRequired
}

AccessTokenForm.defaultProps = {
    fetching: false,
    loading: false,
    appSource: [],
    plans: {
        content: []
    }
}

export default Form.create({})(AccessTokenForm)