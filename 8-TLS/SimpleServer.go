package main

import (
	"net/http"
	"log"
)

func HelloServer(w http.ResponseWriter, req *http.Request) {
	w.Header().Set("Content-Type", "text/plain")
	w.Write([]byte("Hello internet!"))
}

func main() {
	http.HandleFunc("/", HelloServer)
	err := http.ListenAndServeTLS(":443", "/etc/letsencrypt/live/tls.aul12.me/fullchain.pem", "/etc/letsencrypt/live/tls.aul12.me/privkey.pem", nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}

