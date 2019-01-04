import { isNumber } from 'util'

function verifyIfExistOrderDuplicate(interceptorsOrder) {

    const orders = new Set(interceptorsOrder)
    return orders.size !== interceptorsOrder.length
}

export function orderIsWrong(interceptors) {
    if (!!interceptors.length ){
        const orders = interceptors.map(i => i.order)
        orders.sort((a, b) => a-b)
        return verifyIfExistOrderDuplicate(orders) || orders.some((order, index) => order !== index)
    }

    return false
}

export function updateOrder(interceptors) {
    if (interceptors) {
        interceptors.forEach((interceptor, index) => {
            interceptor.order = index
        })
    }
}


export function changeOrder(orderDrag, newOrder, interceptors) {

    const allInterceptors = JSON.parse(JSON.stringify(interceptors))

    if (orderIsWrong(allInterceptors)){
        updateOrder(allInterceptors)
    }

    let newIndexInterceptor = 0
    let indexInterceptor = 0

    if (isNumber(newOrder)) {
        newIndexInterceptor = allInterceptors.findIndex(element => element.id === newOrder)
    } else {
        newIndexInterceptor = allInterceptors.findIndex(element => element.uuid === newOrder)
    }
    
    if (isNumber(orderDrag)) {
        indexInterceptor = allInterceptors.findIndex(element => element.id === orderDrag)
    } else {
        indexInterceptor = allInterceptors.findIndex(element => element.uuid === orderDrag)
    }

    let changedInterceptors = []

    allInterceptors[indexInterceptor].order = allInterceptors[newIndexInterceptor].order
    changedInterceptors.push(allInterceptors[indexInterceptor])

    if (indexInterceptor < newIndexInterceptor) {
        for (let i = newIndexInterceptor; i > indexInterceptor; i--) {
            allInterceptors[i].order = allInterceptors[i].order - 1
            changedInterceptors.push(allInterceptors[i])
        }
    } else {
        for (let i = newIndexInterceptor; i < indexInterceptor; i++) {
            allInterceptors[i].order = allInterceptors[i].order + 1
            changedInterceptors.push(allInterceptors[i])
        }
    }

    if (orderIsWrong(interceptors)) {
        changedInterceptors = allInterceptors
    }

    return changedInterceptors
}
