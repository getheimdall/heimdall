const fs = require('fs')
const readLine = require('readline')

fs.writeFileSync('./env-config.js', 'window._env_ = { \n')

const readInterface = readLine.createInterface({
    input: fs.createReadStream('.env')
})

readInterface.on('line', line => {
    if (line.includes("=")) {
        const regex = /(.+)=(.+)/
        const name = regex.exec(line)[1].trim()
        let value = regex.exec(line)[2].trim()

        const valueFromEnv = process.env[name]

        if (valueFromEnv) {
            value = valueFromEnv
        }

        fs.appendFileSync('./env-config.js', `  ${name}:"${value}",\n`)
    }
})

readInterface.on('close', () => {
    fs.appendFileSync('./env-config.js', '}')

    const pathIndex = process.argv.slice(2)[0]
    console.log(pathIndex)
    if (pathIndex) {
        let indexHtml = fs.readFileSync(pathIndex, 'utf8')
        const indexContentFinal = indexHtml.replace(/(.+)(env-config\.js)(\?\d+)?(.+)/, `$1$2?${new Date().getTime()}$4`)
        console.log(indexContentFinal)
        fs.writeFileSync(pathIndex, indexContentFinal)
        console.log('updated index')
    }
})