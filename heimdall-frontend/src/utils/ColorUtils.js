const getColorMethod = (method) => {
    let color;
    if (method === 'GET') {
        color = '#61affe'
    } else if (method === 'POST') {
        color = '#49cc90'
    } else if (method === 'DELETE') {
        color = '#f93e3e'
    } else if (method === 'PUT') {
        color = '#fca130'
    } else if (method === 'PATCH') {
        color = '#50e3c2'
    }

    return color;
}

const getColorStatus = (status) => {
    let color;
    status = Math.floor(status / 100);

    if (status === 1){
        color = '#99c2ff'
    } else if (status === 2) {
        color = '#70ff7b'
    } else if (status === 3) {
        color = '#ffae7a'
    } else if (status === 4) {
        color = '#ff6c6c'
    } else {
        color = '#ce86fe'
    }

    return color;
}

const getColorLevel = (level) => {
    let color;

    if (level === 'INFO') {
        color = '#2950a0'
    } else if (level === 'DEBUG') {
        color = '#6f4bae'
    } else if (level === 'ERROR') {
        color = '#8c0b0c'
    } else if (level === 'WARN') {
        color = '#b65e2f'
    } else {
        color = '#a5b0b9'
    }

    return color;
}

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
