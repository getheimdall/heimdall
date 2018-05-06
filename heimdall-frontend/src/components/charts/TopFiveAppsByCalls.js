import React from 'react'
import {
    PieChart,
    Pie,
    Cell,
    Tooltip,
    Legend,
    ResponsiveContainer
} from 'recharts'

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#CC0000']
const RADIAN = Math.PI / 180
const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent, index }) => {
 	const radius = innerRadius + (outerRadius - innerRadius) * 0.5
    const x  = cx + radius * Math.cos(-midAngle * RADIAN)
    const y = cy  + radius * Math.sin(-midAngle * RADIAN)
 
    return (
        <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} 	dominantBaseline="central">
            {`${(percent * 100).toFixed(0)}%`}
        </text>
    )
}

const TopFiveAppsByCalls = ({data}) => (
    <ResponsiveContainer width="100%" height={300}>
        <PieChart width={800} height={400} onMouseEnter={this.onPieEnter}>
            <Pie
                cy={100}
                data={data} 
                labelLine={false}
                label={renderCustomizedLabel}
                outerRadius={100} 
                fill="#8884d8"
                dataKey="value"
            >
                {
                    data.map((entry, index) => <Cell key={index} fill={COLORS[index % COLORS.length]}/>)
                }
            </Pie>
            <Tooltip />
            <Legend />
        </PieChart>
    </ResponsiveContainer>
)

export default TopFiveAppsByCalls