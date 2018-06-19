import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Link} from 'react-router-dom'

import {Table, Tooltip, Button, Row, Pagination} from 'antd';

const {Column} = Table;

class ListTraces extends Component {

    render() {
        const {dataSource, loading} = this.props
        return (
            <div>
                {console.log(dataSource.content)}
                <Table dataSource={dataSource.content} rowKey={record => record.id} loading={loading}
                       pagination={false}>
                    <Column title="ID" dataIndex="id.counter" id="id"/>
                    <Column title="URL" dataIndex="trace.url" id="url"/>
                    <Column title="Method" dataIndex="trace.method" id="method"/>
                    <Column title="Status" dataIndex="trace.resultStatus" id="status"/>
                    <Column title="Duration" dataIndex="trace.durationMillis" id="duration"/>
                    <Column
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title="Edit">
                                    <Link to={"/traces/" + record.id}><Button type="primary" icon="search"/></Link>
                                </Tooltip>
                            </span>
                        )}
                    />
                </Table>
                <Row type="flex" justify="center" className="h-row">
                    <Pagination total={dataSource.totalElements} onChange={this.props.handlePagination}/>
                </Row>
            </div>
        )
    }
}

ListTraces.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListTraces.defaultProps = {
    dataSource:
        {
            "number": 0,
            "size": 1,
            "totalPages": 1,
            "numberOfElements": 1,
            "totalElements": 1,
            "firstPage": false,
            "hasPreviousPage": false,
            "hasNextPage": true,
            "hasContent": true,
            "first": true,
            "last": false,
            "nextPage": 1,
            "previousPage": 0,
            "content": [
                {
                    "id": {
                        "timestamp": 1529070668,
                        "machineIdentifier": 11183442,
                        "processIdentifier": 11432,
                        "counter": 11228599,
                        "time": 1529070668000,
                        "date": "2018-06-15T13:51:08.000+0000",
                        "timeSecond": 1529070668
                    },
                    "trace": {
                        "method": "GET",
                        "url": "http://localhost:8080/basepath/test",
                        "resultStatus": 200,
                        "durationMillis": 136,
                        "insertedOnDate": "15/06/2018 10:51:08.120",
                        "apiId": 1,
                        "apiName": "Simple API",
                        "app": null,
                        "accessToken": null,
                        "receivedFromAddress": "",
                        "clientId": null,
                        "resourceId": 1,
                        "appDeveloper": null,
                        "operationId": 1,
                        "request": null,
                        "response": null,
                        "pattern": "/test",
                        "traces": [
                            {
                                "description": "Localized mock interceptor",
                                "insertedOnDate": "15/06/2018 10:51:08.136",
                                "content": "\"{\"name\":\"Mock Example\"}\""
                            }
                        ],
                        "filters": [
                            {
                                "name": "HeimdallDecorationFilter",
                                "timeInMillisRun": 6,
                                "timeInMillisShould": 0,
                                "status": "SUCCESS",
                                "totalTimeInMillis": 6
                            },
                            {
                                "name": "OperationMockPre1",
                                "timeInMillisRun": 1,
                                "timeInMillisShould": 1,
                                "status": "SUCCESS",
                                "totalTimeInMillis": 2
                            },
                            {
                                "name": "CustomSendResponseFilter",
                                "timeInMillisRun": 4,
                                "timeInMillisShould": 0,
                                "status": "SUCCESS",
                                "totalTimeInMillis": 4
                            }
                        ],
                        "profile": "developer"
                    },
                    "logger": "mongo",
                    "level": "INFO",
                    "thread": "http-nio-8080-exec-5",
                    "ts": "2018-06-15T13:51:08.274+0000"
                }
            ]
        }
}

export default ListTraces