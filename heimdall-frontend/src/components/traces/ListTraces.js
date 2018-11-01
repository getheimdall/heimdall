import React, {Component} from 'react'
import PropTypes from 'prop-types'
import {Link} from 'react-router-dom'
import {Table, Tooltip, Button, Row, Pagination, Tag} from 'antd'

import i18n from "../../i18n/i18n"
import ColorUtils from "../../utils/ColorUtils"

const {Column} = Table;

class ListTraces extends Component {

    render() {
        const {dataSource, loading} = this.props
        return (
            <div>
                <Table dataSource={dataSource.content} rowKey={record => record.id.toString()} loading={loading}
                       pagination={false}>
                    <Column title={i18n.t('id')} dataIndex="id" id="id"/>
                    <Column title={i18n.t('url')} dataIndex="trace.url" id="url"/>
                    <Column title={i18n.t('method')} dataIndex="trace.method" id="method"/>
                    <Column title={i18n.t('status')} id="trace.resultStatus" key="status" render={(record) => (
                        <span>
                            <Tag color={ColorUtils.getColorStatus(record.trace.resultStatus)}>{record.trace.resultStatus === 'ACTIVE' ? i18n.t('active') : i18n.t('inactive')}</Tag>
                        </span>
                    )} />
                    <Column title={i18n.t('duration')} dataIndex="trace.durationMillis" id="duration"/>
                    <Column
                        title={i18n.t('action')}
                        id="action"
                        key="action"
                        render={(text, record) => (
                            <span>
                                <Tooltip title={i18n.t('view')}>
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
    handlePagination: PropTypes.func.isRequired
}

//used to prototype the table component
ListTraces.defaultProps = {
    dataSource: {}

}

export default ListTraces