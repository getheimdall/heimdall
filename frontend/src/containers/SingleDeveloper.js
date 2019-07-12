//3rd's
import React, { Component } from 'react'
import { push } from 'connected-react-router'
import { connect } from 'react-redux'
//components
import { Card, Row } from 'antd'
// actions
import i18n from "../i18n/i18n"
import Loading from '../components/ui/Loading'
import PageHeader from '../components/ui/PageHeader'
import DeveloperForm from '../components/developers/DeveloperForm'
import { getDeveloper, initLoading, clearDeveloper, clearDevelopers, update, save, remove } from '../actions/developers'

class SingleDeveloper extends Component {

    state = { loadDev: false }

    componentDidMount() {
        let idDev = this.props.match.params.id
        if (idDev) {
            this.props.dispatch(getDeveloper(idDev))
            this.setState({ ...this.state, loadDev: true })
        }
    }

    componentWillUnmount() {
        this.props.dispatch(clearDeveloper())
    }

    handleSubmit = (formObject) => {
        this.props.dispatch(initLoading())
        if (formObject.id) {
            this.props.dispatch(clearDeveloper())
            this.props.dispatch(update(formObject))
        } else {
            this.props.dispatch(save(formObject))
        }
    }

    handleDelete = (developerId) => {
        this.props.dispatch(remove(developerId))
        this.props.dispatch(clearDevelopers())
        this.props.dispatch(push('/developers'))
    }

    render() {
        const { developer } = this.props

        if (this.state.loadDev && !developer) return <Loading />
        const title = developer ? i18n.t('edit') : i18n.t('add')

        return (
            <div>
                <PageHeader title={i18n.t('developers')} icon="code" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' ' + i18n.t('developer')}>
                        <DeveloperForm developer={developer} handleDelete={this.handleDelete} handleSubmit={this.handleSubmit} loading={this.props.loading} />
                    </Card>
                </Row>
            </div>
        )
    }
}

const mapStateToProps = state => {
    return {
        developer: state.developers.developer,
        loading: state.developers.loading
    }
}

export default connect(mapStateToProps)(SingleDeveloper)