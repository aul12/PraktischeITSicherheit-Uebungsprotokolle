---
title: TLS and how to implement it
author: Paul Nykiel
date: \today
---

# Installation & Hello World
## Hello World
*hello-world.go*:
```go
package main

import "fmt"

func main() {
    fmt.Println("Hello World!")
}
```

## Schlüsselerstellung
Für die Schlüsselerstellung wurden die folgenden Befehle verwendet (abgeleitet von folgendem Thread [stackoverflow.com/questions/11992036/how-do-i-create-an-ecdsa-certificate-with-the-openssl-command-line](https://stackoverflow.com/questions/11992036/how-do-i-create-an-ecdsa-certificate-with-the-openssl-command-line)):
```
openssl ecparam -out ec_key.pem -name prime256v1 -genkey
openssl req -new -key ec_key.pem -x509 -nodes -days 365 -out cert.pem
```
dadurch entstehen die beiden Dateien: `ec_key.pem` und `cert.pem`.

## Webserver
Auf Basis von folgendem Beispiel [github.com/denji/golang-tls](https://github.com/denji/golang-tls) 
wurde ein Webserver mit TLS Support implementiert:

*SimpleServer.go*
```go
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
        err := http.ListenAndServeTLS(":443", "cert.pem", "ec_key.pem", nil)
        if err != nil {
                log.Fatal("ListenAndServe: ", err)
        }
}
```

Wenn der Server aufgerufen wird (`--insecure` Flag, da das Zertifikat selbst-signiert ist), erhält man die Nachricht *Hello internet!*:
```bash
$curl --insecure https://localhost/
Hello internet!
```

# Lets Encrypt
## Installation und Key-Erstellung
Nach folgender Anleitung [certbot.eff.org/lets-encrypt/ubuntubionic-other](https://certbot.eff.org/lets-encrypt/ubuntubionic-other) 
wurde Certbot installiert und ein Zertifikat generiert.
Es wurde die Option ohne laufender Webserver gewählt, da der `SimpleServer.go` einfach gestoppt werden kann, außerdem
musst eine DNS Eintrag eingerichtet werden. Das automatische renewal wurde nicht aktiviert, da der Server
nur temporär genutzt wird.

Das Zertifikat bzw. das Key-File sind dann unter 
`/etc/letsencrypt/live/tls.aul12.me/fullchain.pem` bzw. `/etc/letsencrypt/live/tls.aul12.me/privkey.pem` zu finden
(bei anderem DNS-Eintrag ist der Pfad natürlich anders). Die beiden Pfade in `SimpleServer.go` wurden dann entsprechend
angepasst.

## Test
Die Website lässt sich jetzt ohne Zertikatfehler öffnen:

![Website mit Zertifikatsinformation](8-TLS/website.png)

## SSL-Test
Wie schon in der Aufgabenstellung beschrieben erreicht die Website nur ein Resultat von *B*, Grund dafür ist die fehlende
Protokollunterstützung für TLS 1.3.

![Ergebnis des SSL-Tests](8-TLS/ssltest_1.png)

#  Konfiguration & Rating
Der verbesserte Webserver erfolgt wieder auf Basis des gleichen Beispiels 
([github.com/denji/golang-tls](https://github.com/denji/golang-tls)).

Der gesamte Webserver:

*Server.go*
```go
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

```

Nach dem erneuten Testen erreicht der Server ein *A+* Rating:

![Ergebnis des SSL-Tests mit Verbesserungen](8-TLS/ssltest_2.png)
