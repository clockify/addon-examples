# Weather addon example

This is an example of the simplest way of creating an addon. It has front end code and it does not use any back end code.
The example contains a html file, manifest.json and the icon.
We have used python to serve files and ngrok to forward our inner port to a public domain.

## Installation
Pull the code from git and open the HTML file or serve it with any web server.

## Run addon locally
1. Install python on your local machine
2. Go to the root folder of the project
3. Use one liners [python-http](https://gist.github.com/willurd/5720255)

## Set up Ngrok
1. Create free account at [ngrok](https://ngrok.com/)
2. Follow their guideline for installing & running app [docs](https://ngrok.com/docs/getting-started)
3. After generating the url get the new url domain and add it to our manifest.json file

## Open-meteo API
This app uses data from open-meteo, a free open-source Weather API. \
You can read their API [open-meteo](https://open-meteo.com/en/docs)

## Try it out
1. Create a CAKE.com [developer account](https://developer.marketplace.cake.com/signup)
1. Login to your test Clockify instance, navigate to Workspace settings > Integrations
1. Install the add-on through the following URL: https://resources.developer.clockify.me/integration-examples/weather/manifest.json
