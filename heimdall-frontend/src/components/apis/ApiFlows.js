import React, { Component } from 'react'
import { Row, Col, Form, Select, Icon, Tooltip, Card, Button,Tabs } from 'antd'

import {DragDropContext} from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import DnDInterceptor from '../interceptors/DnDInterceptor'
import DropClientInterceptors from '../interceptors/DropClientInterceptors'

const FormItem = Form.Item
const Option = Select.Option
const TabPane = Tabs.TabPane

class ApiFlows extends Component {
    constructor(props) {
        super(props);
        this.state = {
            newFlow: {
                id: 0,
                resource: null,
                operation: null,
                interceptors: []
            },
            interceptorsGroup: [
                {
                    id: 1,
                    category: 'Traffic',
                    icon: 'clock-circle-o',
                    interceptors: [
                        {
                            id: 1,
                            name: 'Access token metric',
                            type: 'client'
                        }, {
                            id: 2,
                            name: 'Billing hits',
                            type: 'backend'
                        }, {
                            id: 3,
                            name: 'Cache invalidation',
                            type: 'backend-client'
                        }, {
                            id: 4,
                            name: 'Cache read',
                            type: 'backend-client'
                        }, {
                            id: 5,
                            name: 'Cache write',
                            type: 'backend'
                        }, {
                            id: 6,
                            name: 'Client ID metric',
                            type: 'client'
                        }
                    ]
                },
                {
                    id: 2,
                    category: 'Traffic',
                    icon: 'clock-circle-o',
                    interceptors: [
                        {
                            id: 1,
                            name: 'Access token metric',
                            type: 'client'
                        }, {
                            id: 2,
                            name: 'Billing hits',
                            type: 'backend'
                        }, {
                            id: 3,
                            name: 'Cache invalidation',
                            type: 'backend-client'
                        }, {
                            id: 4,
                            name: 'Cache read',
                            type: 'backend-client'
                        }, {
                            id: 5,
                            name: 'Cache write',
                            type: 'backend'
                        }, {
                            id: 6,
                            name: 'Client ID metric',
                            type: 'client'
                        }
                    ]
                }
            ]
        }
    }

    render() { 
        const { interceptorsGroup } = this.state
        return (
            <div>
                <Card
                    title="Choose your Flow"
                    extra={<Tooltip title="Add interceptors on your API Flow"><Icon type="question-circle-o" style={{opacity:0.6}} /></Tooltip>}
                    style={{marginBottom:20}}
                    className="inside-shadow"
                >
                    <Form>
                        <Row gutter={20} type="flex" justify="space-between" align="middle">
                            <Col sm={24} md={12}>
                                <FormItem label="Resources">
                                    <Select defaultValue="all" id="resouce_flow">
                                        <Option value="all">All</Option>
                                    </Select>
                                </FormItem>
                            </Col>

                            <Col sm={24} md={12}>
                                <FormItem label="Operations">
                                    <Select defaultValue="all" id="operation_flow">
                                        <Option value="all">All</Option>
                                    </Select>
                                </FormItem>
                            </Col>
                        </Row>
                    </Form>
                </Card>

                <Card
                    title="Interceptors"
                    extra={<Tooltip title="Refresh"><Button type="primary" icon="reload" ghost shape="circle" /></Tooltip>}
                >
                    <Tabs defaultActiveKey="0" style={{marginBottom:20}}>
                        {interceptorsGroup.map((group, i) => (
                            <TabPane className="" tab={group.category} key={i}>
                                {group.interceptors.map(interc => (
                                    <DnDInterceptor
                                        key={interc.id}
                                        name={interc.name}
                                        type={interc.type}
                                        icon={group.icon}
                                    />
                                ))}
                            </TabPane>
                        ))}
                    </Tabs>

                    <DropClientInterceptors />
                </Card>
            </div>
        )
    }
}
 
export default DragDropContext(HTML5Backend)(ApiFlows)