import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Link} from 'react-router-dom'

import {Modal, Row, Table, Divider, Tag, Tooltip, Button, Pagination} from 'antd';
import ComponentAuthority from "../ComponentAuthority";
import { privileges }from "../../constants/privileges-types";

const confirm = Modal.confirm;
const {Column} = Table;

class ListAccessTokens extends Component {

    showDeleteConfirm = (accessTokenId) => (e) => {
        confirm({
            title: 'Are you sure?',
            okText: 'Yes',
            okType: 'danger',
            cancelText: 'No',
            onOk: () => {
                this.props.handleDelete(accessTokenId)
            }
        });
    }

    render() {
        const {dataSource} = this.props
        const {loading} = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x: 1155}} loading={loading}
                       pagination={false}>
                    <Column title="ID" dataIndex="id" id="id" width={200}/>
                    <Column title="Status" id="status" key="status" width={200} render={(record) => (
                        <span>
                            {record.status === 'ACTIVE' && <Tag color="green">{record.status}</Tag>}
                            {record.status === 'INACTIVE' && <Tag color="red">{record.status}</Tag>}
                        </span>
                    )}/>
                    <Column title="Token" dataIndex="code" id="code" width={400}/>
                    <Column title="App" dataIndex="app.name" id="name"/>
                    <Column
                        id="action"
                        key="action"
                        align="right"
                        render={(text, record) => (
                            <span>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_UPDATE_ACCESSTOKEN]}>
                                    <Tooltip title="Edit">
                                        <Link to={"/tokens/" + record.id}><Button type="primary" icon="edit"/></Link>
                                    </Tooltip>
                                </ComponentAuthority>
                                <Divider type="vertical"/>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_ACCESSTOKEN]}>
                                    <Tooltip title="Delete">
                                        <Button type="danger" icon="delete"
                                                onClick={this.showDeleteConfirm(record.id)}/>
                                    </Tooltip>
                                </ComponentAuthority>
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

ListAccessTokens.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListAccessTokens.defaultProps = {
    dataSource:
        {
            "number": 0,
            "size": 1,
            "totalPages": 618,
            "numberOfElements": 1,
            "totalElements": 618,
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
                    "id": 436,
                    "code": "c3dhZ2dlci1lZGl0b3I6c3dhZ2dlci1lZGl0b3I=",
                    "app": {
                        "id": 1,
                        "clientId": "3a3ae2bc-ca0c-3840-9f44-beb5c5fdc3db",
                        "name": "API Manager Integration",
                        "description": "App for API Manager Integration",
                        "creationDate": "2017-12-13T12:56:21.853",
                        "status": "ACTIVE"
                    },
                    "expiredDate": null,
                    "creationDate": "2017-12-13T13:01:38.247",
                    "plans": [
                        {
                            "id": 1,
                            "name": "Plan Pier Migração",
                            "description": "Plano para migração do pier para o heimdall",
                            "creationDate": "2017-12-13T12:56:51.637",
                            "status": "ACTIVE"
                        }
                    ],
                    "status": "ACTIVE"
                }
            ]
        }
}

export default ListAccessTokens