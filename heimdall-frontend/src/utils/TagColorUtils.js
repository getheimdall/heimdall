export default (method) => {
    switch (method) {
        case 'GET':
            return 'blue'
        case 'POST':
            return 'green'
        case 'PUT':
            return 'orange'
        case 'DELETE':
            return 'red'
        default:
            return ''
    }
}

