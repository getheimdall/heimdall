import React from 'react'
import {Form, Button, Col, Input} from 'antd'

import i18n from '../../i18n/i18n'
import {PrivilegeUtils} from '../../utils/PrivilegeUtils'

const FormItem = Form.Item

class PairField extends React.Component {

    state = {
        nameKey: ''
    }

    componentDidMount() {
        const { nameKey } = this.props

        if (nameKey) {
            this.setState({ ...this.state, nameKey: this.props.nameKey })
        }
    }

    handleUpdateKey = event => {
        this.setState({ ...this.state, nameKey: event.target.value })
    }

    render() {
        const { getFieldDecorator } = this.props.form

        return (
            <React.Fragment>
                <Col sm={4} md={11}>
                    <FormItem label={i18n.t('param_name')}>
                        <Input required type="text" value={this.state.nameKey} onChange={(e) => this.handleUpdateKey(e)} disabled={!PrivilegeUtils.verifyPrivileges(this.props.privileges)}/>
                    </FormItem>
                </Col>
                <Col sm={4} md={11}>
                    <FormItem label={i18n.t('param_value')}>
                        {
                            getFieldDecorator(`content.cors.${this.state.nameKey}`, {
                                initialValue: this.props.value,
                                rules: [
                                    { required: true, message: i18n.t('please_input_value')}
                                ]
                            })(<Input type="text" disabled={!PrivilegeUtils.verifyPrivileges(this.props.privileges)}/>)
                        }
                    </FormItem>
                </Col>
                <Col sm={4} md={2}>
                    <FormItem label=" " colon={false}>
                        <Button icon="delete" onClick={() => this.props.remove()} disabled={!PrivilegeUtils.verifyPrivileges(this.props.privileges)}/>
                    </FormItem>
                </Col>
            </React.Fragment>
        )
    }
}

export default PairField