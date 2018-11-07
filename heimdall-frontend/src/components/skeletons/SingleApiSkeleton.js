import React from 'react'
import {Card, Row, Skeleton, Tabs} from 'antd'

import i18n from "../../i18n/i18n"
import PageHeader from "../ui/PageHeader"

const TabPane = Tabs.TabPane

const SingleApiSkeleton = () => (
    <div className="joy">
        <PageHeader title={i18n.t('apis')} icon="api" />
        <Row>
            <Card style={{ width: '100%' }} title={<Skeleton title={{ width: '20%' }} paragraph={false} active/>}>
                <Tabs defaultActiveKey="1" className="resource-tour">
                    <TabPane tab={i18n.t('definitions')} key="1">
                       <Skeleton active title={false} paragraph={{ rows: 4 }}/>
                    </TabPane>
                    <TabPane tab={<div role="tab" className="ant-tabs-tab resource">{i18n.t('resources')}</div>} key="2" >
                        <Skeleton active title={false} paragraph={{ rows: 4 }}/>
                    </TabPane>
                    <TabPane tab={<div role="tab" className="ant-tabs-tab interceptors">{i18n.t('interceptors')}</div>} key="3">
                        <Skeleton active title={false} paragraph={{ rows: 4 }}/>
                    </TabPane>
                    <TabPane tab={<div role="tab" className="ant-tabs-tab middlewares">{i18n.t('middlewares')}</div>} key="4">
                        <Skeleton active title={false} paragraph={{ rows: 4 }}/>
                    </TabPane>
                </Tabs>
            </Card>
        </Row>
    </div>
)

export default SingleApiSkeleton