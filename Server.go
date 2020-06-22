package main

import (
	"net/http"
	"log"
	"crypto/tls"
)


// Aufgabe a)
func loadConfig() *tls.Config {
	cfg := &tls.Config{
		MinVersion:               tls.VersionTLS12,
		CurvePreferences:         []tls.CurveID{
			tls.CurveP521, tls.CurveP384, tls.CurveP256},
		PreferServerCipherSuites: true,
		CipherSuites: []uint16{
			tls.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
			tls.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
			tls.TLS_RSA_WITH_AES_256_GCM_SHA384,
			tls.TLS_RSA_WITH_AES_256_CBC_SHA,
		},
	}
	return cfg
}

// Aufgabe b)
func createServer(port string, routeHandler func(http.ResponseWriter, *http.Request), config *tls.Config) *http.Server{
	mux := http.NewServeMux()
	mux.HandleFunc("/", routeHandler)
	srv := &http.Server{
		Addr:         port,
		Handler:      mux,
		TLSConfig:    config,
		TLSNextProto: make(map[string]func(*http.Server, *tls.Conn, http.Handler), 0),
	}
	return srv
}

// Aufgabe c)
func main() {
	config := loadConfig()
	srv := createServer(":443", handle, config)
	log.Fatal(srv.ListenAndServeTLS("/etc/letsencrypt/live/tls.aul12.me/fullchain.pem", "/etc/letsencrypt/live/tls.aul12.me/privkey.pem"))
}

func handle(w http.ResponseWriter, req *http.Request) {
	w.Header().Add("Strict-Transport-Security", "max-age=63072000; includeSubDomains")
	w.Write([]byte("Hello TLS!"))
}
