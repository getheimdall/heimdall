import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { Modal, Row, Table, Divider, Tag, Tooltip, Button, Pagination } from 'antd'

import i18n from "../../i18n/i18n"

const confirm = Modal.confirm;
const { Column } = Table;

class ListAccessTokens extends Component {

    showDeleteConfirm = (accessTokenId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(accessTokenId)
            }
        });
    }

    render() {
        const { dataSource } = this.props
        const { loading } = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x:1155}} loading={loading} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" width={200} />
                    <Column title={i18n.t('status')} id="status" key="status" width={200} render={(record) => (
                        <span style={{textTransform: 'uppercase'}}>
                            {record.status === 'ACTIVE' && <Tag color="green">{i18n.t('active')}</Tag>}
                            {record.status === 'INACTIVE' && <Tag color="red">{i18n.t('inactive')}</Tag>}
                        </span>
                    )} />
                    <Column title={i18n.t('token')} dataIndex="code" id="code" width={400} />
                    <Column title={i18n.t('app')} dataIndex="app.name" id="name" />
                    <Column
                        title={i18n.t('action')}
                        id="action"
                        key="action"
                        align="right"
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('edit')  }>
                                    <Link to={"/tokens/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <Divider type="vertical" />
                                <Tooltip title={i18n.t('delete')}>
                                    <Button type="danger" icon="delete" onClick={this.showDeleteConfirm(record.id)} />
                                </Tooltip>
                            </span>
                        )}
                    />
                </Table>
                <Row type="flex" justify="center" className="h-row">
                    <Pagination total={dataSource.totalElements} onChange={this.props.handlePagination} />
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