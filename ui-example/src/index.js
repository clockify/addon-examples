const express = require('express')
const bodyParser = require('body-parser')

const port = process.env.NODE_PORT || 8080

const app = express()

app.use(bodyParser.json())
app.use(express.static('static'))

app.post('/lifecycle/installed', (req, res) => {
    console.log(req.body, "installed");
    res.send('Got a POST request')
})

app.post('/lifecycle/uninstalled', (req, res) => {
    console.log(req.body, "uninstalled");
    res.send('Got a POST request')
})

app.post('/lifecycle/settings-updated', (req, res) => {
    console.log(req.body, "settings-updated");
    res.send('Got a POST request')
})

app.listen(port, () => {
    console.log(`App listening on port ${port}`)
})
