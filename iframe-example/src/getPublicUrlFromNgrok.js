const ngrok = require('ngrok');
const { config } = require('./config')

module.exports.getPublicUrlFromNgrok = async function() {
  await ngrok.connect({ addr: config.port, authtoken: config.ngrok_auth_token }).catch(err => console.info("ERR"))
  const ngrokApi = ngrok.getApi();
  const tunnels = await ngrokApi.listTunnels()
  const initialPublicUrl = tunnels.tunnels[tunnels.tunnels.length - 1].public_url
  return !initialPublicUrl.startsWith("https") ? initialPublicUrl.replace("http", "https") : initialPublicUrl
}
