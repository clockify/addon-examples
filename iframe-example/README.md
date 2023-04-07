# iframe example Add-on
This addon is an example of storing a url on the settings page and using that to display an iframe on sidebar.

The manifest can be obtained on `GET {addonUrl}/manifest-v0.1.json`.

Developed using NodeJS, Docker. This addon doesn't not use any sdks, just plain HTML to display the iframe and a static manifest (static/manifest-v0.1.json) file to define settings page and the sidebar entrypoint.

## How to run this addon locally

You need docker installed and a ngrok auth token that can be found on [ngrok dashboard](https://dashboard.ngrok.com/get-started/your-authtoken).

```
cp .env.example .env
```
Edit .env and include your token on `NGROK_AUTH_TOKEN=(your token here)`
```
docker compose up
```

After that the container is up, use the url provided on the console to register the addon. This addon comes up with ngrok and generates the public url by itself.
