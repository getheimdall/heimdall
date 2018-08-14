import React from 'react'
import {Table, Input, Row, Popconfirm, Tooltip, Button, Form} from 'antd'
import ComponentAuthority from "../ComponentAuthority";
import {privileges} from "../../constants/privileges-types";
import {PrivilegeUtils} from "../../utils/PrivilegeUtils";

const FormItem = Form.Item
const ButtonGroup = Button.Group;
let columns = [{
    title: 'Key',
    dataIndex: 'k',
    width: '47%'
}, {
    title: 'Value',
    dataIndex: 'v',
    width: '47%'
}];

const PrepareButtonsOperation = (key, buttonAdd, add, remove) => {
    return (
        <FormItem>
            <ButtonGroup>
                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_ENVIRONMENT]}>
                    <Tooltip title="Delete">
                        <Popconfirm title="Sure to delete?" onConfirm={() => remove(key)}>
                            <Button type="danger" icon="delete"/>
                        </Popconfirm>
                    </Tooltip>
                </ComponentAuthority>

                {PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])
                && buttonAdd ?
                    <Tooltip title="New">
                        <Button type="primary" icon="plus" onClick={() => add()}/>
                    </Tooltip>
                    : ''
                }
            </ButtonGroup>
        </FormItem>
    )
}

const PrepareInput = (elementId, val, form) => {
    const {getFieldDecorator} = form;
    return (
        <FormItem>
            {getFieldDecorator(elementId, {
                    initialValue: val,
                    rules: [{
                        required: true,
                        message: 'Please input value!'
                    }
                    ]
                }
            )(<Input disabled={!PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])}/>)}
        </FormItem>
    )
}

class ListVariablesEnvironment extends React.Component {

    render() {
        const {getFieldDecorator, getFieldValue} = this.props.form;
        const {variables, add, remove, form} = this.props
        let data = []

        if (PrivilegeUtils.verifyPrivileges([privileges.PRIVILEGE_CREATE_ENVIRONMENT, privileges.PRIVILEGE_UPDATE_ENVIRONMENT])) {
            columns = [{
                title: 'Key',
                dataIndex: 'k',
                width: '47%'
            }, {
                title: 'Value',
                dataIndex: 'v',
                width: '47%'
            }, {
                title: '',
                dataIndex: 'operation'
            }];
        }

        if (variables) {
            let enableButton = false

            getFieldDecorator('variablesCount', {initialValue: variables});
            const variablesCount = getFieldValue('variablesCount');

            variablesCount.forEach((value, key) => {

                if (variablesCount[key] === variablesCount[variablesCount.length - 1]) {
                    enableButton = true
                }

                data.push({
                    key: key,
                    k: PrepareInput(`variables[${key}].key`, variables[key] === undefined ? '' : variables[key]['key'], form),
                    v: PrepareInput(`variables[${key}].value`, variables[key] === undefined ? '' : variables[key]['value'], form),
                    operation: PrepareButtonsOperation(key, enableButton, add, remove)
                });
            });

        }
        console.log(columns)

        return (
            <Row>
                <Table columns={columns} dataSource={data} pagination={false} bordered/>
            </Row>
        )
    }
}

export default ListVariablesEnvironment
