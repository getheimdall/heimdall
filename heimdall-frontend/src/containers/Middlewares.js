import React, {Component} from 'react'
import {connect} from 'react-redux'
import {Button, Card, Icon, Input, message, notification, Pagination, Row, Table, Tag, Tooltip, Upload} from 'antd'

import {downloadMiddleware, getMiddlewares, initLoading, save} from '../actions/middlewares'
import i18n from "../i18n/i18n"

const Dragger = Upload.Dragger
const Column = Table.Column

class Middlewares extends Component {

    state = {
        version: "",
        page: 0,
        pageSize: 10
    }

    componentDidMount() {
        this.props.dispatch(initLoading())
        this.props.dispatch(getMiddlewares({offset: 0, limit: 10}, this.props.api.id))
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    handleChangeUpload = (info) => {
        const status = info.file.status
        if (status === 'done') {
            this.setState({...this.state, version: ""})
            message.success(`${info.file.name} ${i18n.t('file_uploaded_successfully')}`)
            this.props.dispatch(initLoading())
            this.props.dispatch(getMiddlewares({offset: this.state.page, limit: 10}, this.props.api.id))
        } else if (status === 'error') {
            message.error(`${info.file.name} ${i18n.t('file_upload_failed')}`)
            message.error(info.file.error.response.data.message)
        }
    }

    handlePagination = (page, pageSize) => {
        this.setState({...this.state, page: page - 1, pageSize: pageSize})
        this.props.dispatch(initLoading())
        this.props.dispatch(getMiddlewares({offset: page - 1, limit: 10}, this.props.api.id))
    }

    handleBeforeUpload = (file) => {
        const splitName = file.name.split('.')
        const type = splitName[splitName.length - 1]
        const isJar = type === "jar"

        if (!isJar) {
            message.error(i18n.t('you_can_only_upload_jar_file'))
            return false
        }

        const isLtOrE25M = file.size / 1024 / 1024 <= 25;

        if (!isLtOrE25M) {
            message.error(i18n.t('jar_must_smaller_or_equal_than_25_mb'))
            return false
        }

        return true
    }

    sendFileUpload = (info) => {
        const {version} = this.state
        const data = new FormData()
        data.append("file", info.file)
        data.append("version", version)
        data.append("status", "ACTIVE")

        this.props.dispatch(save(data, this.props.api.id, info))
    }

    handleOnChangeVersion = (event) => {
        this.setState({...this.state, version: event.target.value})
    }

    handleFileDownload = (middlewareId) => {
        this.props.dispatch(initLoading())
        this.props.dispatch(downloadMiddleware(middlewareId, this.props.api.id))
    }

    verifyVersionIsEmpty = () => {
        return this.state.version.length === 0
    }

    render() {

        const {loading, middlewares} = this.props

        const propsFileUpload = {
            name: 'file',
            multiple: false,
            onChange: this.handleChangeUpload,
            beforeUpload: this.handleBeforeUpload,
            accept: '.jar',
            customRequest: this.sendFileUpload
        };

        return (
            <div>
                <Card
                    title={i18n.t('upload_middleware')}
                    style={{marginBottom: 20}}
                    className="inside-shadow"
                >
                    <Row>
                        <Input placeholder={i18n.t('version')} onChange={(event) => this.handleOnChangeVersion(event)}
                               addonBefore={i18n.t('version')} value={this.state.version}/>
                    </Row>
                    <br/>
                    <Row>
                        <Dragger id="dragMiddleware" {...propsFileUpload} disabled={this.verifyVersionIsEmpty()}>
                            <p className="ant-upload-drag-icon">
                                <Icon type="inbox"/>
                            </p>
                            <p className="ant-upload-text">{i18n.t('click_or_drag_middleware_to_this_area_to_upload')}</p>
                            <p className="ant-upload-hint">{i18n.t('support_for_single_upload_at_time')}</p>
                        </Dragger>
                    </Row>
                </Card>
                <Card
                    title={i18n.t('version_middleware')}
                    style={{marginBottom: 20}}
                    className="inside-shadow"
                >
                    {
                        middlewares &&
                        <div>
                            <Table dataSource={middlewares.content} loading={loading} rowKey={record => record.id}
                                   pagination={false} >
                                <Column title={i18n.t('id')} dataIndex="id" id="id"/>
                                <Column title={i18n.t('path')} dataIndex="path" id="path"/>
                                <Column title={i18n.t('version')} dataIndex="version" id="version"/>
                                <Column title={i18n.t('status')} id="status" key="status" render={(record) => (
                                    <span>
                                        {record.status === 'ACTIVE' && <Tag color="green">{i18n.t('active')}</Tag>}
                                        {record.status === 'INACTIVE' && <Tag color="red">{i18n.t('inactive')}</Tag>}
                                    </span>
                                )}/>
                                <Column title={i18n.t('created_on')} dataIndex="creationDate"/>
                                <Column title={i18n.t('download')} render={(record) => (
                                    <Tooltip title={i18n.t('download_this_file')}>
                                        <Button className="card-button add-tour" type="primary" icon="download" onClick={() => this.handleFileDownload(record.id)} size="large" shape="circle" />
                                    </Tooltip>
                                )}/>
                            </Table>
                            <Row type="flex" justify="center" className="h-row">
                                <Pagination total={middlewares.totalElements} onChange={this.handlePagination}/>
                            </Row>
                        </div>
                    }

                </Card>
            </div>
        )
    }
}


const mapStateToProps = state => {
    return {
        middlewares: state.middlewares.middlewares,
        loading: state.middlewares.loading,
        notification: state.middlewares.notification
    }
}

export default connect(mapStateToProps)(Middlewares)