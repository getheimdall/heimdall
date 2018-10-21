import React from 'react'
import PropTypes from 'prop-types'
import ReactEcharts from 'echarts-for-react'

import Loading from "./Loading"

class Chart extends React.Component {

    state = {
        color: 'red'
    }

    componentDidMount() {
        this.setState({...this.state, color: this.props.color})
    }

    getOption = () => {
        const { metrics } = this.props

        let dataMetrics = []
        let dataMetricsValues = []

        if (metrics && Array.isArray(metrics)) {

            dataMetrics = metrics.map(metric => {
                return metric.metric
            })

            dataMetricsValues = metrics.map(metric => {
                return metric.value
            })

        }

        return {
            title: {
                text: this.props.title,
            },
            tooltip: {},
            xAxis: {
                data: dataMetrics
            },
            yAxis: {},
            series: [{
                name: 'requests',
                type: 'bar',
                data: dataMetricsValues
            }],
            color: this.state.color

        }
    }

    render() {

        const { metrics } = this.props

        if (!metrics) {
            return <Loading />
        }

        return (
            <ReactEcharts option={this.getOption()} style={{height: 350}}/>
        )
    }
}

Chart.propTypes = {
    metrics: PropTypes.array,
    title: PropTypes.string.isRequired,
}

export default Chart
