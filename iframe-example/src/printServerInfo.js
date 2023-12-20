const clc = require("cli-color");
const {config} = require("./config");

module.exports.printServerInfo = () => {
    const manifestPublicUrl =  `${config.url}/${config.manifestName}`

    console.log('\n\n')
    console.log(clc.magenta('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'))
    console.log('\n')
    console.log(clc.blue('Manifest is running on:'), clc.green(manifestPublicUrl), '\n')
    console.log(clc.blue("You can add it to your Clockify test instance, available from the \nDeveloper Portal at:"), clc.green('https://developer.marketplace.cake.com/'))
    console.log('\n')
    console.log(clc.magenta('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'))
    console.log('\n')
}
