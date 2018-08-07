//3rd's
import React, { Component } from 'react'
import { push } from 'connected-react-router';
import { connect } from 'react-redux'

// actions
import { getDeveloper, initLoading, clearDeveloper, clearDevelopers, update, save, remove } from '../actions/developers';

//components
import { Card, Row } from 'antd'
import PageHeader from '../components/ui/PageHeader'
import Loading from '../components/ui/Loading'
import DeveloperForm from '../components/developers/DeveloperForm';

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
        const title = developer ? 'Edit' : 'Add'

        return (
            <div>
                <PageHeader title="Developers" icon="code" />
                <Row className="h-row bg-white">
                    <Card style={{ width: '100%' }} title={title + ' Developer'}>
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