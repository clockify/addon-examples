## Hello World

This is a simple addon which will render a widget component that when clicked will display a 'Hello World!' message (or a custom one).

### How it works
The embedded webserver is started, and handlers are registered for the lifecycle and the UI component.

A MongoDB database is used to store the data for an addon installation (workspace info, settings etc).

### Getting started
#### Requirements
- A Github account and an access token associated with it.
- Docker

#### Running the addon with docker
The addon can be run using the provided docker compose file.

You should update the PUBLIC_URL environment variable from the docker-compose.yml to reflect the actual value.

First, we build the image by passing in a Github username and it's access token.
These are only used in order to pull the Addons SDK dependency from Github packages.

Then we run the container and pass in the addon public URL.
The container will expose the following port for the addon: 8080.

Use the following commands to run the addon app:
```shell
docker-compose build --build-arg GITHUB_USERNAME="{username}" --build-arg GITHUB_TOKEN="{token}"
docker-compose up
```

<br>


The Server class is the entrypoint to the addon application.

The addon makes use of the following environment variables:

```
ADDON_KEY=helloworld
ADDON_NAME=Hello World
ADDON_DESCRIPTION=A sample addon that renders a UI component

PUBLIC_URL=
LOCAL_PORT={port}
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
```displayed-text```
The custom text to be displayed.

### Structure
#### Handlers
The addon makes use of several HTTP handlers:
- Lifecycle handlers (installed, uninstalled, settings updated)
- UI Component handler

The first group is intended to handle lifecycle events and store related information linked to the lifecycle of the addon.
The latter is intended to render the requested UI component.

#### Repository
The addon installation data are stored in a MongoDB instance.

#### Addon
The HelloWorldAddon class contains all the information related to the addon.