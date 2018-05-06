import React from 'react'
import { Select, Row, Icon, Card, Button, Tag } from 'antd'

const Option = Select.Option;

const ApiInfo = ({api}) => (
    <div>
        <Row style={{ marginTop: 30 }}>
            <Select style={{ width: '100%' }} defaultValue="production">
                <Option value="production" key={1}>[PRODUÇÃO] {api.name}</Option>
                <Option value="sandbox" key={2}>[SANDBOX] {api.name}</Option>
                <Option value="production-sensedia" key={3}>PRODUCTION SENSEDIA - GATEWAY V3</Option>
                <Option value="sandbox-sensedia" key={4}>SANDBOX SENSEDIA - GATEWAY V3</Option>
            </Select>
        </Row>

        <Row style={{ marginTop: 30 }}>
            <h4 style={{opacity: 0.6}}><Icon type="bar-chart" /> Environments stats</h4>
            <Card
                title="Apps"
                extra={<Icon type="laptop" />}
            >
                <h2 style={{opacity: 0.7}}>30</h2>
            </Card>

            <Card
                title="Calls"
                extra={<Icon type="global" />}
                style={{marginTop: 20}}
            >
                <h2 style={{opacity: 0.7, marginBottom: 0}}>31337</h2>
                <p><small>Last 30 days</small></p>
            </Card>
        </Row>

        <Row style={{ marginTop: 30 }}>
            <Button.Group style={{width: '100%'}}>
                <Button type="primary" style={{width: '50%'}}>
                    <Icon type="sync" /> Trace
                </Button>

                <Button type="primary" style={{width: '50%'}}>
                    <Icon type="pie-chart" /> Analytics
                </Button>
            </Button.Group>
        </Row>

        <Row style={{ marginTop: 30 }}>
            <h4 style={{opacity: 0.6}}><Icon type="profile" /> Plans</h4>
            {api.plans
                ? api.plans.map(plan => (
                    <Tag color="blue" key={plan.id}>{plan.title}</Tag>
                ))
                : null
            }
        </Row>
    </div>
);

export default ApiInfo