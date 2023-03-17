# UI example Add-on
This addon is an example of every entrypoint for an addon to add custom UI.

The manifest can be obtained on `GET {addonUrl}/manifest-v0.1.json`.

Developed using NodeJS, Docker. This addon doesn't not use any sdks, just plain Node to handle lifecycle events and a static manifest (static/manifest-v0.1.json) file to define UI entrypoints and lifecycle configuration.

## UI entrypoints

This add-on is displayed in all currently supported locations.

1. Sidebar
2. Time off tab
3. Schedule tab
4. Approvals tab
5. Activity tab
6. Team tab
7. Projects tab
8. Widget
9. Reports tab
10. Settings tab

On tab locations you can add more than one tab, in this example we've only used one tab per location. The time off tab contains an example with charts and widget is a chat example. Settings shows every UI element that we support via the manifest configuration. Every other entrypoint shows custom UI elements that can be used via the official [UI library](https://resources.developer.clockify.me/ui/latest/css/main.min.css) .

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

