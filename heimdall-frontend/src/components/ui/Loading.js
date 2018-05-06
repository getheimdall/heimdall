import React from 'react'

import { Row, Col, Spin } from 'antd'

const Loading = () => (
    <Row type="flex" justify="center" align="bottom">
        <Col style={{marginTop: 20}}>
            <Spin size="large" tip="Loading...">

            </Spin>
        </Col>
    </Row>
)

export default Loading