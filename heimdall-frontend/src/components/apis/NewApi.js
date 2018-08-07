import React, { Component } from 'react'
import { bindActionCreators } from 'redux'
import { connect } from 'react-redux'
import PageHeader from '../ui/PageHeader'
import { Row, Button, Card, notification } from 'antd'

import { getAllEnvironments, clearEnvironments } from '../../actions/environments'
import { saveApi, getNewApi } from '../../actions/apis'

import NewApiOverview from './NewApiOverview'
import Loading from '../ui/Loading'

class NewApi extends Component {
    constructor(props) {
        super(props)

        this.state = {
            current: 0
        }

        this.next = this.next.bind(this)
        this.prev = this.prev.bind(this)
    }

    componentWillReceiveProps(newProps) {
        if (newProps.notification && newProps.notification !== this.props.notification) {
            const { type, message, description } = newProps.notification
            notification[type]({ message, description })
        }
    }

    componentDidMount() {
        this.props.clearEnvironments()
        this.props.getAllEnvironments()
        if (!this.props.newApi) {
            this.props.apiInit()
        }
    }

    componentWillUnmount() {
        this.props.clearEnvironments()
    }

    validateSubmition(next) {
        if (next === 0) {
            this.overview.onSubmitOverview()
        }
        if (next === 1) {
            this.resources.completeResource()
        }
    }

    next(savedValues = null) {
        const current = this.state.current + 1;

        if (savedValues !== null && savedValues.newApi) {
            this.setState({ newApi: savedValues.newApi })
        }

        this.setState({ current: current });
    }

    prev() {
        const current = this.state.current - 1;
        this.setState({ current });
    }

    render() {


        const { newApi } = this.props
        const { history } = this.props
        
        if (!this.props.environments || !this.props.newApi) return <Loading />

        return (
            <div>
                <PageHeader title="APIs" icon="api" />

                <Row>
                    <div className="steps-content">
                        <Card>
                            <NewApiOverview api={newApi} environments={this.props.environments} onRef={ref => (this.overview = ref)} next={this.next} submit={this.props.saveApi} />
                        </Card>
                    </div>
                </Row>

                <Row className="h-row">
                    <div className="steps-action">
                        <Button ghost type="primary" style={{ marginRight: 8 }} onClick={() => history.goBack()} >Cancel</Button>
                        <Button type="primary" onClick={() => this.validateSubmition(this.state.current)}>Save</Button>
                    </div>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = (state) => {

    let optionsEnvs;
    if (state.environments.environments) {
        optionsEnvs = state.environments.environments.map((env, index) => {
            const labered = env.name + ' [' + env.inboundURL + ']'
            return { label: labered, value: env.id }
        })
    }

    return {
        newApi: state.apis.api,
        notification: state.apis.notification,
        environments: optionsEnvs
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        getAllEnvironments: bindActionCreators(getAllEnvironments, dispatch),
        saveApi: bindActionCreators(saveApi, dispatch),
        apiInit: bindActionCreators(getNewApi, dispatch),
        clearEnvironments: bindActionCreators(clearEnvironments, dispatch),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(NewApi);