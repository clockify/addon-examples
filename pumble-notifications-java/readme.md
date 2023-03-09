# Pumble Notifications

This is a simple addon which listens to events from Clockify webhooks, and forwards these events to a Pumble channel.

### How it works
The embedded webserver is started, and handlers are registered to listen for incoming events.

A MongoDB database is used to store the data for an addon installation (workspace info, settings etc).

Once an event is received, it is processed and then POSTed to the Pumble webhook endpoint that the user has configured.

### Getting started
#### Requirements
- A Github account and an access token associated with it
- Docker

#### Running the addon with docker
The addon can be run using the provided docker compose file.

You should update the PUBLIC_URL environment variable from the docker-compose.yml to reflect the actual value.

First, we build the image by passing in a Github username and it's access token.
These are only used in order to pull the Addon SDK dependency from Github packages.

Then, we run the container and pass in the addon public URL.
The container will expose the following port for the addon: 8080.

Use the following commands to run the addon app:
```shell
docker-compose build --build-arg GITHUB_USERNAME="{username}" --build-arg GITHUB_TOKEN="{token}"
docker-compose up
```

This addon example serves the manifest under the following path:
```
{baseUrl}/manifest
```

### Required environment variables
The Server class is the entrypoint to the addon application.

The addon makes use of the following environment variables:

```
ADDON_KEY=pumblenotifications
ADDON_NAME=Pumble Notifications
ADDON_DESCRIPTION=A sample addon that registers Clockify webhooks and then posts a message to the configured Pumble channel whenever the events are triggered.

PUBLIC_URL=
MONGO_URI=
MONGO_DATABASE=
LOCAL_PORT=8080
```
### Retrieving a public URL
The addon must be accessible through a public URL in order for Clockify to be able to communicate with it.

For this example we made use of a free service called <a href="https://ngrok.com">ngrok</a>.

After downloading the binary, we can execute the following command which will expose the server running on our local port through a public URL.
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