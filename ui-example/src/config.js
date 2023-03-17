module.exports.config = {
  port: process.env.NODE_PORT || 8080,
  ngrok_auth_token: process.env.NGROK_AUTH_TOKEN || ""
}
