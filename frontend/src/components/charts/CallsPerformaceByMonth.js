import React from 'react'
import {
    ComposedChart,
    Line,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts'

const DataComposedChart = ({data}) => (
    <ResponsiveContainer width="100%" height={250}>
        <ComposedChart
            width={500}
            height={400}
            data={data}
            margin={{top: 20, right: 20, bottom: 20, left: 0}}
        >
            <XAxis dataKey="name"/>
            <YAxis />
            <Tooltip/>
            <Legend/>
            <CartesianGrid stroke='#f5f5f5'/>
            <Bar name='Total Calls' dataKey='tc' fill='#1890ff'/>
            <Line name='Average latency' type='monotone' dataKey='al' stroke='#ff7300'/>
        </ComposedChart>
    </ResponsiveContainer>
)

export default DataComposedChart