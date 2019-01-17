import React from 'react'
import {Link} from "react-router-dom"
import {Button, Divider, Modal, Pagination, Row, Table, Tooltip} from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../policy/ComponentAuthority"
import {privileges} from "../../constants/privileges-types"

const confirm = Modal.confirm
const Column = Table.Column

class ListProviders extends React.Component {

    showDeleteConfirm = (providerId) => (e) => {
        confirm({
            title: i18n.t('are_you_sure'),
            okText: i18n.t('yes'),
            okType: 'danger',
            cancelText: i18n.t('no'),
            onOk: () => {
                this.props.handleDelete(providerId)
            }
        });
    }

    render() {

        const { dataSource, loading } = this.props

        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id} scroll={{x:694}} loading={loading} pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id" />
                    <Column title={i18n.t('name')} dataIndex="name" id="name" />
                    <Column title={i18n.t('description')} dataIndex="description" id="description" />
                    <Column title={i18n.t('path')} dataIndex="path" id="path" />
                    <Column
                        title={i18n.t('action')}
                        id="action"
                        key="action"
                        render={(text, record) => (
                            !record.providerDefault &&
                            <span>
                                <Tooltip title={i18n.t('edit')}>
                                    <Link to={"/providers/" + record.id}><Button type="primary" icon="edit" /></Link>
                                </Tooltip>
                                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_DELETE_PROVIDER]}>
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

export default ListProviders