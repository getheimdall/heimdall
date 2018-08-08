import React, {Component} from 'react'
import {connect} from 'react-redux'
import PageHeader from '../ui/PageHeader'
import ListApis from './ListApis'
// import FloatButton from '../ui/FloatButton'
import {Button, Col, Form, Input, notification, Row, Tooltip} from 'antd'
import {getAllApis} from '../../actions/apis'
import Loading from '../ui/Loading';
import ComponentAuthority from "../ComponentAuthority";
import { privileges } from '../../constants/privileges-types'

const FormItem = Form.Item

class Index extends Component {
    constructor(props) {
        super(props)
        this.state = {searchBy: ''}
        this.searchApi = this.searchApi.bind(this)
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const {type, message, description} = newProps.notification
            notification[type]({message, description})
        }
    }

    componentDidMount() {
        this.props.dispatch(getAllApis())
        this.setState({
            iconLoading: false
        })
    }

    searchApi(evt) {
        this.setState({...this.state, searchBy: evt.target.value})
    }

    render() {
        const {apis} = this.props
        const {history} = this.props

        let listApi = null
        if (!apis) {
            listApi = <Loading/>
        } else {
            const filteredApis = apis.filter(item => item.name.toUpperCase().includes(this.state.searchBy.toUpperCase()))
            listApi = <ListApis apis={filteredApis} history={history}/>
        }
        return (
            <div>
                <PageHeader title="APIs" icon="api"/>
                <Row className="h-row bg-white search-api">
                    <Form layout="inline" id="api-search-form">
                        <Row gutter={20} type="flex" justify="space-between" align="bottom">
                            <Col sm={24} md={24}>
                                <FormItem style={{width: '100%'}}>
                                    <Input id="api_keyword" className="teste" placeholder="Enter an keyword api"
                                           onChange={this.searchApi}/>
                                </FormItem>
                            </Col>
                        </Row>
                    </Form>
                </Row>

                {listApi}
                <ComponentAuthority privilegesAllowed={[privileges.PRIVILEGE_CREATE_API]}>
                    <Tooltip placement="left" title="Add new API">
                        <Button style={{position: 'fixed', bottom: '30px', right: '30px', zIndex: 9}}
                                className="floatButton" type="primary" icon="plus"
                                onClick={() => history.push("/apis/new")}
                                size="large" shape="circle"/>
                    </Tooltip>
                </ComponentAuthority>
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