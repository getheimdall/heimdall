import React from 'react'
import ReactTable from 'react-table'
import tagColor from '../../utils/TagColorUtils'
import { Select, Tag, Switch, Button, Tooltip } from 'antd'

const Option = Select.Option

const ListResourceOperations = ({
    operations,
    editOperation,
    deleteOperation,
    changeAsync
}) => {
    return (
        <ReactTable 
            data={operations}
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
                }, {
                    Header: 'Async',
                    accessor: 'async',
                    style: {textAlign: 'center'},
                    maxWidth: 100,
                    Cell: row => (
                        <Switch defaultChecked={row.value} onChange={e => changeAsync(e, row)} />
                    )
                }, {
                    Header: '',
                    maxWidth: 100,
                    style: {textAlign: 'center'},
                    Cell: row => (
                        <div>
                            <Tooltip title="Edit operation">
                                <Button icon="edit" shape="circle" size="small" style={{margin:'0 4px'}} onClick={(e) => editOperation(e, row)} />
                            </Tooltip>
                            <Tooltip title="Delete operation">
                                <Button icon="delete" shape="circle" size="small" style={{margin:'0 4px'}} onClick={e => deleteOperation(e, row)} />
                            </Tooltip>
                        </div>
                    )
                }
            ]}
        />
    )
}

export default ListResourceOperations