import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { Modal, Row, Table, Divider, Tag, Tooltip, Button, Pagination } from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {privileges} from "../../constants/privileges-types"

const confirm = Modal.confirm;
const { Column } = Table;

class ListApps extends Component {

    showDeleteConfirm = (appId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(appId)
            }
        });
    }

    render() {
        const { dataSource } = this.props
        const { loading } = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} loading={loading} scroll={{x: 990}} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" width={90} />
                    <Column title={i18n.t('name')} dataIndex="name" id="name" />
                    <Column title={i18n.t('description')} dataIndex="description" id="name" />
                    <Column title={i18n.t('client_id')} dataIndex="clientId" id="clientId" width={150} />
                    <Column title={i18n.t('developer')} dataIndex="developer.name" id="developer" />
                    <Column title={i18n.t('status')} id="status" key="status" render={(record) => (
                        <span style={{textTransform: 'uppercase'}}>
                            {record.status === 'ACTIVE' && <Tag color="green">{i18n.t('active')}</Tag>}
                            {record.status === 'INACTIVE' && <Tag color="red">{i18n.t('inactive')}</Tag>}
                        </span>
                    )} />
                    <Column
                        align="right"
                        id="action"
                        key="action"
                        width={200}
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('edit')}>
                                    <Link to={"/apps/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <Divider type="vertical" />
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_APP]}>
                                    <Tooltip title={i18n.t('delete')}>
                                        <Button type="danger" icon="delete" onClick={this.showDeleteConfirm(record.id)} />
                                    </Tooltip>
                                </ComponentAuthority>
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

ListApps.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListApps.defaultProps = {
    dataSource:
        [
            {
                "id": 369,
                "clientId": "Y3IOMl11y0ob",
                "name": "Banco Online",
                "description": "Integração com a carteira virtual",
                "developer": {
                    "id": 1,
                    "name": "Alex Akira Almodovar Kubo",
                    "email": "alex.kubo@modal.com.br",
                    "creationDate": "2017-12-13T12:55:56.353",
                    "status": "ACTIVE"
                },
                "creationDate": "2017-12-13T12:56:50.4",
                "status": "ACTIVE"
            },
            {
                "id": 379,
                "clientId": "12123çlk123çl",
                "name": "nome de teste",
                "description": "teste",
                "developer": {
                    "id": 1,
                    "name": "Alex Akira Almodovar Kubo",
                    "email": "alex.kubo@modal.com.br",
                    "creationDate": "2017-12-13T12:55:56.353",
                    "status": "ACTIVE"
                },
                "creationDate": "2017-12-29T09:25:02.24",
                "status": "ACTIVE"
            }
        ]
}

export default ListApps