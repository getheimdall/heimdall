import React from 'react'
import {Button, List, Row, Skeleton} from 'antd'

const ButtonGroup = Button.Group
const operationsFiltered = [ {}, {}, {}, {}, {} ]

const ListOperationSkeleton = () => (
    <div style={{ position: 'relative' }}>
        <Row type="flex" justify="center">
            <div align="center">
                <Skeleton title={{width: 200}} paragraph={false} active />
            </div>
        </Row>
        <List
            className="demo-loadmore-list"
            itemLayout="horizontal"
            dataSource={operationsFiltered}
            renderItem={operation => {
                return (
                    <List.Item>
                        <List.Item.Meta
                            avatar={
                                <Skeleton avatar={{ size: 'large' }} title={false} paragraph={false} active/>
                            }
                            title={<Skeleton title={{ width: '20%' }} paragraph={false} active />}
                            description={<Skeleton title={{width: '50%'}} paragraph={false} active />}
                        />
                        <Row type="flex" justify="center">
                            <ButtonGroup>
                                <Skeleton title paragraph={false} active/>
                            </ButtonGroup>
                        </Row>
                    </List.Item>
                )
            }}
        />
    </div>
)

export default ListOperationSkeleton