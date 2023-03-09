package main

import (
	"log"
	"net/http"
	"os"
)

func main() {
	// register a handler to server all the static files under the public directory
	http.Handle("/public/", http.StripPrefix("/public/", http.FileServer(http.Dir("./public"))))
	handleUiComponent()

	// start a http server on port 8080
	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		panic(err)
	}
}

func handleUiComponent() {
	// load the html content from the template
	uiBytes, err := os.ReadFile("./templates/ui.html")
	if err != nil {
		log.Fatalf("unable to read file: %v", err)
	}

	// register a handler that writes the template bytes into the response writer
	http.HandleFunc("/ui", func(w http.ResponseWriter, r *http.Request) {
		_, err := w.Write(uiBytes)
		if err != nil {
			w.WriteHeader(500)
		} else {
			w.WriteHeader(200)
		}
	})
}
