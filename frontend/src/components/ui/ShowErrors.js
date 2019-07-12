import React from 'react'

const ShowErrors = ({errors}) => (
    <ul>
        {errors.map(error => {
            <li>{error}</li>
        })}
    </ul>
)

export default ShowErrors