const { app } = require("../createWebserver");
const { config } = require("../config");
const manifest = require("../manifest-v0.1.json");

manifest["baseUrl"] = config.url;

app.get(`/${config.manifestName}`, (req, res) => {
  res.send(manifest);
});
