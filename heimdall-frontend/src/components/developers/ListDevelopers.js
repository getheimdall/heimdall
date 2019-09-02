import React, { Component } from 'react'
import PropTypes from 'prop-types'
import { Link } from 'react-router-dom'
import { Modal, Row, Table, Divider, Tag, Tooltip, Button, Pagination } from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {privileges} from "../../constants/privileges-types"

const confirm = Modal.confirm;
const { Column } = Table;

class ListDevelopers extends Component {

    showDeleteConfirm = (environmentId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(environmentId)
            }
        });
    }

    render() {
        const { dataSource } = this.props
        const { loading } = this.props
        return (    
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x: 672}} loading={loading} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" />
                    <Column title={i18n.t('name')} dataIndex="name" id="name" />
                    <Column title={i18n.t('email')} dataIndex="email" id="email" />
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
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('edit')}>
                                    <Link to={"/developers/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_DEVELOPER]}>
                                    <Divider type="vertical" />
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

ListDevelopers.propTypes = {
    dataSource: PropTypes.object.isRequired,
    handleDelete: PropTypes.func.isRequired,
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListDevelopers.defaultProps = {
    dataSource:
        [{
            id: '1',
            name: 'John John',
            email: 'email1@modal.com.br',
            status: 'ACTIVE',
        }, {
            id: '2',
            name: 'Jim Parson',
            email: 'email2@modal.com.br',
            status: 'ACTIVE',
        }, {
            id: '3',
            name: 'Joe Tolanski',
            email: 'email2@modal.com.br',
            status: 'INACTIVE',
        },
        {
            id: '4',
            name: 'Jo Orski',
            email: 'email3@modal.com.br',
            status: 'ACTIVE',
        }]
}

export default ListDevelopers