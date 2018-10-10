const getFilters = () => {
    return [
        {
            name: "trace.method",
            type: "type",
            operations: getOperations("type"),
            possibleValues: getPossibleValues("method"),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Method"
        },
        {
            name: "trace.url",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "URL"
        },
        {
            name: "trace.resultStatus",
            type: "number",
            operations: getOperations("number"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Response status"
        },
        {
            name: "trace.durationMillis",
            type: "number",
            operations: getOperations("number"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Request duration time"
        },
        {
            name: "trace.insertedOnDate",
            type: "date",
            operations: getOperations("date"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Date"
        },
        {
            name: "trace.apiId",
            type: "numberId",
            operations: getOperations("numberId"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Api ID"
        },
        {
            name: "trace.apiName",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Api name"
        },
        {
            name: "trace.app",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "App"
        },
        {
            name: "trace.accessToken",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Access token"
        },
        {
            name: "trace.receivedFromAddress",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Received from"
        },
        {
            name: "trace.clientId",
            type: "numberId",
            operations: getOperations("numberId"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Client ID"
        },
        {
            name: "trace.resourceId",
            type: "numberId",
            operations: getOperations("numberId"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Resource ID"
        },
        {
            name: "trace.appDeveloper",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "App developer"
        },
        {
            name: "trace.operationId",
            type: "numberId",
            operations: getOperations("numberId"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Operation ID"
        },
        {
            name: "trace.pattern",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Request pattern"
        },
        {
            name: "trace.profile",
            type: "string",
            operations: getOperations("string"),
            possibleValues: getPossibleValues(""),
            operationSelected: "",
            firstValue: "",
            secondValue: "",
            label: "Profile"
        }
    ]
}

const getOperations = (type) => {

    switch (type) {
        case "string":
            return [
                "contains",
                "equals",
                "not equals",
                "none",
                "all"
            ]
        case "number":
            return [
                "equals",
                "not equals",
                "between",
                "less than",
                "greater than",
                "less than or equals",
                "greater than or equals",
                "none",
                "all"
            ]
        case "numberId":
            return [
                "equals",
                "not equals",
                "none",
                "all"
            ]
        case "type":
            return [
                "equals",
                "not equals",
                "none",
                "all"
            ]
        case "date":
            return [
                "equals",
                "not equals",
                "between",
                "less than",
                "greater than",
                "less than or equals",
                "greater than or equals",
                "today",
                "yesterday",
                "this week",
                "last week",
                "this month",
                "last month",
                "this year",
                "none",
                "all"
            ]
        default:
            return []
    }
}

const getPossibleValues = (field) => {

    switch (field) {
        case "method":
            return [
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
            ]
        case "level":
            return [
                "INFO",
                "DEBUG",
                "WARN",
                "ERROR"
            ]
        default:
            return []
    }
}

export default {
    getFilters,
    getOperations,
    getPossibleValues
}