package assignment.attacks;

import java.io.IOException;
import java.net.Socket;

public class RangeHeader {
    static String host = "127.0.0.1";
    static int port = 12345;
    static int threads;

    // TODO: add necessary fields if needed

    public static void main(String[] args) {

        try {
            openSocket();
        } catch (IOException e) {
            System.out.println("Unable to connect. Is server running?");
            e.printStackTrace();
        }

        //TODO: construct list of ranges for range header
        //TODO: send multiple valid GET requests for 'antwort.png' with range header field
    }

    //TODO: Task 1 - Send request with appropriate header and query
    private static void openSocket() throws IOException {
        Socket socket = new Socket(host, port);
    }
}