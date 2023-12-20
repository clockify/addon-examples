const { app } = require("./createWebserver");
const { config } = require("./config");
const { printServerInfo } = require("./printServerInfo");

(async () => {
  // Include endpoints
  require("./endpoints");

  // Start server
  app.listen(config.port, () => {});

  // Print server info
  printServerInfo();
})();
