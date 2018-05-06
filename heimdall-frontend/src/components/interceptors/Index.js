import React, { Component } from 'react'
import PageHeader from '../ui/PageHeader'
import SubTitle from '../ui/SubTitle'
import { Collapse, Row, Col, Icon } from 'antd'

const Panel = Collapse.Panel

const customPanelStyle = {
    background: '#ffffff',
    marginBottom: 24,
    border: 0,
    overflow: 'hidden',
};

class Index extends Component {
    constructor(props) {
        super(props);
        this.state = {
            interceptors: {
                defaults: [
                    {
                        id: 1,
                        title: 'Traffic',
                        icon: 'clock-circle-o'
                    }, {
                        id: 2,
                        title: 'Security',
                        icon: 'safety'
                    }, {
                        id: 3,
                        title: 'Mediation',
                        icon: ''
                    }, {
                        id: 4,
                        title: 'Tracing'
                    }, {
                        id: 5,
                        title: 'Transformation'
                    }
                ],
                custom: [
                    {
                        id: 1,
                        title: 'Uncategorized'
                    }
                ]
            }
        }
    }

    render() { 
        const { interceptors } = this.state
        return (
            <div>
                <PageHeader
                    title="Interceptors"
                    icon="filter"
                />
                <Row gutter={20} className="h-row">
                    <Col sm={24} md={12}>
                        <SubTitle title="Default Interceptors" description="You can't edit or delete the defaults" />
                        <Collapse bordered={false} style={{ backgroundColor: 'transparent' }}>
                            {
                                interceptors.defaults && interceptors.defaults.length > 0
                                ? interceptors.defaults.map((interceptor, index) => (
                                    <Panel 
                                        showArrow={false} 
                                        header={(<h4 style={{marginBottom:0}}>{interceptor.title}</h4>)} 
                                        style={customPanelStyle} key={index} 
                                        className="bottom-shadow"
                                    >
                                        <p></p>
                                    </Panel>
                                ))
                                : null
                            }
                        </Collapse>
                    </Col>

                    <Col sm={24} md={12}>
                        <SubTitle title="Custom Interceptors" description="Click it to open Advanced Custom Interceptor Editor" />
                        <Collapse bordered={false} style={{ backgroundColor: 'transparent' }}>
                            {
                                interceptors.custom && interceptors.custom.length > 0
                                ? interceptors.custom.map((interceptor, index) => (
                                    <Panel
                                        showArrow={false}
                                        header={(<h4 style={{marginBottom:0}}>{interceptor.title}</h4>)}
                                        style={customPanelStyle}
                                        className="bottom-shadow"
                                    >
                                        <p></p>
                                    </Panel>
                                ))
                                : null
                            }
                        </Collapse>
                    </Col>
                </Row>
            </div>
        )
    }
}
 
export default Index