import React, { Component } from 'react'
import { Row, Col, Card, Collapse, Tooltip, Button } from 'antd'
import PageHeader from './ui/PageHeader'
import update from 'react-addons-update'

import CallsPerformaceByMonth from './charts/CallsPerformaceByMonth'
import TokensPerformanceByMonth from './charts/TokensPerformanceByMonth'
import SuccessCallsVersusErros from './charts/SuccessCallsVersusErros'
import TopFiveAppsByCalls from './charts/TopFiveAppsByCalls'
import HeimdallCollapse from './collapse';

const HeimdallPanel = HeimdallCollapse.Panel
const Panel = Collapse.Panel

class Home extends Component {
    constructor(props) {
        super(props)
        this.handleCard = this.handleCard.bind(this) // util

        // migrate to redux store
        this.state = {
            cards: [
                {
                    id: 1,
                    title: "Calls Performance by month",
                    visible: true,
                    flagChart: "CallsPerformaceByMonth",
                    data: [
                        { name: 'Nov/2017', tc: 590, al: 800 },
                        { name: 'Dec/2017', tc: 868, al: 967 },
                        { name: 'Jan/2018', tc: 1397, al: 200 },
                        { name: 'Feb/2018', tc: 1480, al: 1200 }
                    ]
                }, {
                    id: 4,
                    title: "Tokens performance by month",
                    visible: true,
                    flagChart: "TokensPerformanceByMonth",
                    data: [
                        { name: 'Nov/2017', tc: 590, al: 800 },
                        { name: 'Dec/2017', tc: 868, al: 967 },
                        { name: 'Jan/2018', tc: 1397, al: 200 },
                        { name: 'Feb/2018', tc: 1480, al: 1200 }
                    ]
                }, {
                    id: 2,
                    title: "Success calls versus errors",
                    visible: true,
                    flagChart: "SuccessCallsVersusErros",
                    data: [
                        { name: '29/01', uv: 4000, pv: 2400, amt: 2400 },
                        { name: '31/01', uv: 3000, pv: 1398, amt: 2210 },
                        { name: '02/02', uv: 2000, pv: 9800, amt: 2290 },
                        { name: '04/02', uv: 2780, pv: 3908, amt: 2000 },
                        { name: '06/02', uv: 1890, pv: 4800, amt: 2181 },
                        { name: '08/02', uv: 2390, pv: 3800, amt: 2500 },
                        { name: '10/02', uv: 3490, pv: 4300, amt: 2100 },
                    ]
                }, {
                    id: 3,
                    title: "Top 5 apps by calls",
                    visible: true,
                    flagChart: "TopFiveAppsByCalls",
                    data: [
                        { name: '[Produção] Plataforma Serviço', value: 400 },
                        { name: '[Produção] App Pernambucanas', value: 300 },
                        { name: '[Produção] Callsystem PCPL', value: 300 },
                        { name: '[Produção] Metabusca', value: 200 },
                        { name: 'app-teste', value: 100 }
                    ]
                }
            ]
        }
    }

    // this method should be a util
    handleCard(e) {
        const { cards } = this.state
        const cardId = e.target.value
        const cardIndex = cards.findIndex(card => card.id === cardId)
        const newState = update(cards, { [cardIndex]: { visible: { $set: e.target.checked } } })
        this.setState({ cards: newState })
    }

    render() {
        const { cards } = this.state
        const visibles = cards.filter(card => card.visible === true)

        const extraPanel =
            <Row justify="left">
                <Button shape="circle" type="primary" icon="ellipsis" ghost />

                {/* <Tooltip title="Add Resource"> */}
                    {/* teste */}
                    {/* <Button className="card-button" type="primary" icon="plus" onClick={this.addResourceModal} size="large" shape="circle" /> */}
                {/* </Tooltip> */}
            </Row>

        return (
            <div>
                <PageHeader
                    title="Summary"
                    icon="line-chart"
                    items={cards}
                    handleCard={this.handleCard}
                />

                {/* <Row className="h-row toggled-items" gutter={20}>
                    {
                        visibles
                            ? visibles.map((card, index) => (
                                <Col sm={24} md={12} className={`card-${card.id}`} style={{ marginBottom: 20 }} key={index}>
                                    <Card
                                        title={card.title}
                                        hoverable={true}
                                    >
                                        {card.flagChart === "CallsPerformaceByMonth" ? (<CallsPerformaceByMonth data={card.data} />) : null}

                                        {card.flagChart === "TokensPerformanceByMonth" ? (<TokensPerformanceByMonth data={card.data} />) : null}

                                        {card.flagChart === "SuccessCallsVersusErros" ? (<SuccessCallsVersusErros data={card.data} />) : null}

                                        {card.flagChart === "TopFiveAppsByCalls" ? (<TopFiveAppsByCalls data={card.data} />) : null}
                                    </Card>
                                </Col>
                            ))
                            : null
                    }
                </Row> */}
            </div>
        )
    }
}

export default Home