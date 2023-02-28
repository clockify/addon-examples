## Pumble Notifications

This is a simple addon which listens to events from Clockify webhooks, and forwards these events to a Pumble channel.

### How it works
The embedded webserver is started, and handlers are registered to listen for incoming events.

A MongoDB database is used to store the data for an addon installation (workspace info, settings etc).

Once an event is received, it is processed and then POSTed to the Pumble webhook endpoint that the user has configured.

### Getting started
The Server class is the entrypoint to the addon application.

In order to run the server, there is a number of environment variables that have to be defined.
Below are the contents of a sample .env file:
```
ADDON_KEY=pumblenotifications
ADDON_NAME=Pumble Notifications
ADDON_DESCRIPTION=A sample addon that registers Clockify webhooks and then posts a message to the configured Pumble channel whenever the events are triggered.

PUBLIC_URL=
LOCAL_PORT=8080
MONGO_URI=mongodb://root:123456@localhost:27017/?authSource=admin
MONGO_DATABASE=pumble-notifications
```

The addon must be accessible through a public URL in order for Clockify to be able to communicate with it.

For this example we made use of a free service called <a href="https://ngrok.com">ngrok</a>.

After downloading the binary, we can execute the following command which will expose our server running on our local port through a public URL.
```shell
ngrok http 8080
```

We then pass the public URL that ngrok provides as an env variable:
```
PUBLIC_URL={ngrok public url}
```

### Settings
```pumble-webhook```
the webhook endpoint where the events will be forwarded

### Structure
#### Handlers
The addon makes use of several HTTP handlers:
- Lifecycle handlers (installed, uninstalled, settings updated)
- Webhook handler

The first group is intended to handle lifecycle events and store related information linked to the lifecycle of the addon.
The latter is intended to handle and forward the received events.

#### Repository
The addon installation data are stored in a MongoDB instance.

#### Addon
The NotificationAddon class contains all the information related to the addon.