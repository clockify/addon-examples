## Hello World

This is a simple addon which will render a widget component that when clicked will display a 'Hello World!' message (or a custom one).

### Getting started
This addon example statically serves the manifest.json file under the following path:
```
{baseUrl}/public/manifest.json
```

The only required change before getting started is to update the 'baseUrl' value inside the ./public/manifest.json file.
See below for a quickstart on how to retrieve a public URL for our example.

Running the addon app:
```shell
go run main.go
```

The above command will run a http server on port 8080.

#### Retrieving a public URL
For this example we made use of a free service called <a href="https://ngrok.com">ngrok</a>.

After downloading the binary, we can execute the following command which will expose the server running on our local port through a public URL.
```shell
ngrok http 8080
```

Last, set the public URL that ngrok provides inside the manifest.json file:
```json
{
  "baseUrl": ""
}
```

### Settings
```displayed-text```
The custom text to be displayed.

### Structure
#### Handlers
The addon makes use of the following HTTP handlers:
- Static files handler (for serving the 'public' directory)
- UI Component handler
