import React from 'react'
import {Card, Row, Skeleton} from "antd"

import i18n from "../../i18n/i18n"
import PageHeader from "../ui/PageHeader"

const SingleAppSkeleton = () => (
    <div>
        <PageHeader title={i18n.t('apps')} icon="appstore" />
        <Row className="h-row bg-white">
            <Card style={{ width: '100%' }} title={<Skeleton title={{width: 200}} paragraph={false} active />}>
                <Skeleton title={{ width: 100 }} paragraph={{ rows: 1, width: '100%' }} active />
                <Skeleton title={{ width: 100 }} paragraph={{ rows: 1, width: '100%' }} active />
                <Skeleton title={{ width: 100 }} paragraph={{ rows: 1, width: '100%' }} active />
                <Skeleton title={{ width: 100 }} paragraph={{ rows: 1, width: '100%' }} active />
                <br/>
                <Skeleton paragraph={{ rows: 5 }}/>
                <div align="right">
                    <Skeleton title={{ width: 100 }} paragraph={false} active />
                </div>
            </Card>
        </Row>
    </div>
)

export default SingleAppSkeleton