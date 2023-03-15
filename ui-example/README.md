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

On tab locations you can add more than one tab, in this example we've only used one tab per location. The time off tab contains an example with charts. Settings shows every UI element that we support via the manifest configuration. Every other entrypoint shows custom UI elements that can be used via the official [UI library](https://resources.developer.clockify.me/ui/latest/css/main.min.css) .

## How to run this addon locally

You need docker installed and ngrok and then you need to run:

```
docker compose up
```

After that the container is up, you need to forward the local env to a URL that can be accessible for installation.

```
ngrok http 8080
```

Change the base url on the manifest configuration `static/manifest-v0.1.json` to the one the ngrok is providing.

The URL that needs to be used is `[ngrok-url]/manifest-v0.1.json`.
