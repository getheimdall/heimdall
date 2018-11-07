import React from 'react'
import {Card, Col, Row, Skeleton} from "antd"

import i18n from "../../i18n/i18n"
import PageHeader from "../ui/PageHeader"

const SingleUserSkeleton = () => (
    <div>
        <PageHeader title={i18n.t('users')} icon="user" />
        <Row className="h-row bg-white">
            <Card style={{ width: '100%' }} title={<Skeleton active title={{width: 200}} paragraph={false} />}>
                <Row gutter={24}>
                    <Col sm={24} md={12} >
                        <Skeleton active title={{ width: 100 }} paragraph={{ rows: 1, width: '100%'}} />
                    </Col>
                    <Col sm={24} md={12} >
                        <Skeleton active title={{ width: 100 }} paragraph={{ rows: 1, width: '100%'}} />
                    </Col>
                    <Col sm={24} md={12} >
                        <Skeleton active title={{ width: 100 }} paragraph={{ rows: 1, width: '100%'}} />
                    </Col>
                    <Col sm={24} md={12} >
                        <Skeleton active  title={{ width: 100 }} paragraph={{ rows: 1, width: '100%'}} />
                    </Col>
                    <Col sm={24} md={12} >
                        <Skeleton active title={{ width: 100 }} paragraph={{ rows: 1, width: '100%'}} />
                    </Col>
                    <Col sm={24} md={12} >
                        <Skeleton active title={{ width: 100 }} paragraph={{ rows: 1, width: '100%'}} />
                    </Col>
                </Row>
                <div align="right">
                    <Skeleton active title={{ width: 100 }} paragraph={false} />
                </div>
            </Card>
        </Row>
    </div>
)

export default SingleUserSkeleton