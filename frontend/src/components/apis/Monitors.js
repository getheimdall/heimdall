import React, { Component } from 'react'
import PageHeader from '../ui/PageHeader'
import ReactTable from 'react-table'
import { Card, Button, Row, Tag, Select } from 'antd'

const Option = Select.Option

class Monitors extends Component {
    render() { 
        const { history, location } = this.props
        const api = location.state.monitor
        const tagColor = (method) => {
            switch (method) {
                case 'GET':
                    return 'blue'
                case 'POST':
                    return 'green'
                case 'PUT':
                    return 'orange'
                case 'DELETE':
                    return 'red'
                default:
                    return ''
            }
        }

        return (
            <div>
                <PageHeader
                    title="APIs"
                    icon="api"
               />
                <Card
                    title="Monitor"
                    extra={<Button type="primary" icon="left" onClick={() => history.goBack()} >Go back</Button>}
                >
                    <Row className="h-row no-mobile-padding">
                        <ReactTable
                            data={api}
                            minRows={1}
                            columns={[
                                {
                                    Header: 'Method',
                                    accessor: 'method',
                                    filterable: true,
                                    filterMethod: (filter, row) => {
                                        if (filter.value === 'get') {
                                            return row[filter.id] === 'GET'
                                        }
                                        if (filter.value === 'post') {
                                            return row[filter.id] === 'POST'
                                        }
                                        if (filter.value === 'put') {
                                            return row[filter.id] === 'PUT'
                                        }
                                        if (filter.value === 'delete') {
                                            return row[filter.id] === 'DELETE'
                                        }
                                        return true
                                    },
                                    Filter: ({filter, onChange}) => (
                                        <Select
                                            defaultValue={filter ? filter.value : 'all'}
                                            onChange={e => onChange(e)}
                                            style={{width: '100%'}}
                                        >
                                            <Option value="all">All</Option>
                                            <Option value="get">GET</Option>
                                            <Option value="post">POST</Option>
                                            <Option value="put">PUT</Option>
                                            <Option value="delete">DELETE</Option>
                                        </Select>
                                    ),
                                    maxWidth: 100,
                                    Cell: row => (
                                        <Tag color={tagColor(row.value)}>{row.value}</Tag>
                                    )
                                }, {
                                    Header: 'Path',
                                    accessor: 'path',
                                    filterable: true,
                                }, {
                                    Header: 'Description',
                                    accessor: 'description',
                                    filterable: true,
                                }
                            ]}
                        />
                    </Row>
                </Card>
            </div>
        )
    }
}

export default Monitors