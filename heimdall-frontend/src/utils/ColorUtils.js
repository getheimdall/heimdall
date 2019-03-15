const getColorMethod = (method) => {

    const colors = {
        'GET': '#61affe',
        'POST': '#49cc90',
        'DELETE': '#f93e3e',
        'PUT': '#fca130',
        'PATCH': '#50e3c2'
    };
    return colors[method] ? colors[method] : '#ce86fe';
};

const getColorStatus = (status) => {
    status = Math.floor(status / 100);

    const colors = {
        1: '#99c2ff',
        2: '#56b63f',
        3: '#ce86fe',
        4: '#ffae7a'
    };

    return colors[status] ? colors[status] : '#ff4444';
};

const getColorLevel = (level) => {

    const colors = {
        'INFO': '#2950a0',
        'DEBUG':'#6f4bae',
        'ERROR':'#8c0b0c',
        'WARN':'#b65e2f'
    };

    return colors[level] ? colors[level] : '#a5b0b9';
};

const getColorActivate = (active) => {

    if (active === 'ACTIVE') {
        return 'green'
    } else {
        return 'red'
    }
}

export default {
    getColorMethod,
    getColorLevel,
    getColorStatus,
    getColorActivate
}
