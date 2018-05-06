const path = require('path')
const fs = require('fs')
const lessToJs = require('less-vars-to-js')
const rewireLess = require('react-app-rewire-less')
const themeVariables = lessToJs(fs.readFileSync(path.join(__dirname, './src/theme.less'), 'utf8'))
const { injectBabelPlugin } = require('react-app-rewired')

module.exports = function override(config, env) {
    config = injectBabelPlugin(['import', { libraryName: 'antd', style: true }], config)
    config = rewireLess.withLoaderOptions({
        modifyVars: themeVariables
    })(config, env)
    return config
}