const express = require('express')
const bodyParser = require('body-parser')
const { getPublicUrlFromNgrok } = require('./getPublicUrlFromNgrok')
const ngrok = require('ngrok')
const { config } = require('./config')
const manifest = require('./manifest-v0.1.json');
const clc = require("cli-color");

const manifestName = 'manifest-v0.1.json';

;(async () => {
    const publicUrl = await getPublicUrlFromNgrok()

    const manifestPublicUrl =  `${publicUrl}/${manifestName}`
    manifest["baseUrl"] = publicUrl 

    const app = express()

    app.use(bodyParser.json())
    app.use(express.static('static'))

    app.get('/manifest-v0.1.json', (req, res) => {
        res.send(manifest)
    })

    app.post('/lifecycle/installed', (req, res) => {
        console.log(req.body, "installed");
        res.send('got a post request')
    })

    app.post('/lifecycle/uninstalled', (req, res) => {
        console.log(req.body, "uninstalled");
        res.send('got a post request')
    })

    app.post('/lifecycle/settings-updated', (req, res) => {
        console.log(req.body, "settings-updated");
        res.send('got a post request')
    })

    app.listen(config.port, () => {
        // console.log(`app listening on port ${config.port}`)
    })

    console.log('\n\n')
    console.log(clc.magenta('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'))
    console.log('\n')
    console.log(clc.blue('Manifest is running on:'), clc.green(manifestPublicUrl), '\n')
    console.log(clc.blue("You can add it to your Clockify test instance, available from the \nDeveloper Portal at:"), clc.green('https://developer.marketplace.cake.com/'))
    console.log('\n')
    console.log(clc.magenta('~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~'))
    console.log('\n')
})();

process.once('SIGUSR2', async function() {
    await ngrok.kill()
    process.kill(process.pid, 'SIGUSR2');
});



