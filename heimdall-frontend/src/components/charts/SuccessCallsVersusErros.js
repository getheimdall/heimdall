import React from 'react'
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts'

const SuccessCallsVersusErrors = ({data}) => (
    <ResponsiveContainer width="100%" height={250}>
        <LineChart
            width={500}
            height={300}
            data={data}
            margin={{top: 5, right: 30, left: 10, bottom: 5}}
        >
            <XAxis dataKey="name"/>
            <YAxis yAxisId="left" />
            <YAxis yAxisId="right" orientation="right" />
            <CartesianGrid strokeDasharray="3 3"/>
            <Tooltip/>
            <Legend />
            <Line name="Success" yAxisId="left" type="monotone" dataKey="pv" stroke="#1890ff" activeDot={{r: 8}}/>
            <Line name="Error" yAxisId="right" type="monotone" dataKey="uv" stroke="#CC0000" />
            <Line name="Redirect" yAxisId="right" type="monotone" dataKey="amt" stroke="#2ca02c" />
        </LineChart>
    </ResponsiveContainer>
)

export default SuccessCallsVersusErrors