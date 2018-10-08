import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Row, Col, Form, Input, Button, Tooltip, notification } from 'antd'

import i18n from "../../i18n/i18n"
import { getAllApis } from '../../actions/apis'
import ListApis from './ListApis'
import PageHeader from '../ui/PageHeader'
import Loading from '../ui/Loading'
// import FloatButton from '../ui/FloatButton'

const FormItem = Form.Item

class Index extends Component {
    constructor(props) {
        super(props)
        this.state = { searchBy: '' }
        this.searchApi = this.searchApi.bind(this)
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentDidMount() {
        this.props.dispatch(getAllApis())
        this.setState({
            iconLoading: false
        })
    }

    searchApi(evt) {
        this.setState({ ...this.state, searchBy: evt.target.value })
    }

    render() {
        const { apis } = this.props
        const { history } = this.props

        let listApi = null
        if (!apis) {
            listApi = <Loading />
        } else {
            const filteredApis = apis.filter(item => item.name.toUpperCase().includes(this.state.searchBy.toUpperCase()))
            listApi = <ListApis apis={filteredApis} history={history} />
        }
        return (
            <div>
                <PageHeader title={i18n.t('apis')} icon="api" />
                <Row className="h-row bg-white search-api">
                    <Form layout="inline" id="api-search-form">
                        <Row gutter={20} type="flex" justify="space-between" align="bottom">
                            <Col sm={24} md={24}>
                                <FormItem style={{ width: '100%' }}>
                                    <Input id="api_keyword" className="teste" placeholder={i18n.t('enter_keyword_api')} onChange={this.searchApi} />
                                </FormItem>
                            </Col>
                        </Row>
                    </Form>
                </Row>

                {listApi}

                <Tooltip placement="left" title={i18n.t('add_new_api')}>
                    <Button id="addApi" style={{ position: 'fixed', bottom: '30px', right: '30px', zIndex: 9 }} className="floatButton" type="primary" icon="plus" onClick={() => history.push("/apis/new")} size="large" shape="circle" />
                </Tooltip>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        apis: state.apis.allApis,
        notification: state.apis.notification,
    }
}

export default connect(mapStateToProps)(Index)