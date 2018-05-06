import React, { Component } from 'react'
import PageHeader from '../ui/PageHeader'
import ReactTable from 'react-table'
import { Card, Button, Row, List } from 'antd'

const listItemStyle = {
    paddingLeft: 16,
    paddingRight: 16
}

class ApiInterceptors extends Component {
    constructor(props) {
        super(props)
        this.state = { interceptors: '' }
    }
    render() { 
        const { history, location } = this.props
        const interceptors = location.state.interceptors

        return (
            <div>
                <PageHeader
                    title="APIs"
                    icon="api"
               />
               <Card
                    title="Interceptors"
                    extra={<Button type="primary" icon="left" onClick={() => history.goBack()} >Go back</Button>}
               >
                    <Row className="h-row no-mobile-padding">
                        <ReactTable
                            data={interceptors}
                            minRows={1}
                            className="-striped -highlight"
                            showPaginationBottom={false}
                            columns={[
                                {
                                    Header: 'Path',
                                    accessor: 'path',
                                    filterable: true
                                }, {
                                    Header: 'Description',
                                    accessor: 'description',
                                    filterable: true
                                }
                            ]}
                            SubComponent={row => (
                                <List
                                    size="small"
                                    dataSource={row.original.routines}
                                    renderItem={item => (<List.Item style={listItemStyle}>{item.title}</List.Item>)}
                                />
                            )}
                        />
                    </Row>
               </Card>
            </div>
        )
    }
}
 
export default ApiInterceptors

