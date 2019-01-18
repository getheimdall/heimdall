import React from 'react'
import {Button, Divider, Table, Tooltip} from 'antd'

import i18n from "../../i18n/i18n"
import ComponentAuthority from "../ComponentAuthority"
import {privileges} from "../../constants/privileges-types"

const Column = Table.Column

class ListProviderParams extends React.Component {

    render() {
        <div>
            <Table dataSource={dataSource} rowKey={record => record.id} scroll={{x:694}} loading={loading} pagination={false}>
                <Column title={i18n.t('name')} dataIndex="name" id="name" />
                <Column title={i18n.t('location')} dataIndex="location" id="location" />
                <Column title={i18n.t('value')} dataIndex="value" id="value" />
                <Column
                    title={i18n.t('action')}
                    id="action"
                    key="action"
                    render={(text, record) => (
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
        </div>
    }
}

export default ListProviderParams