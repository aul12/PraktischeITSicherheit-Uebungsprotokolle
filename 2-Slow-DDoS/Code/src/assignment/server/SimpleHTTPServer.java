package assignment.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.DataFormatException;

/**
 * Demo webserver to showcase Slow DDoS attacks (Slowloris, RUDY, Range-Header).
 */
public class SimpleHTTPServer {

    static int port = 12345;
    static int threadPoolSize = 10;

    public static void main (String[] args) throws IOException {
        // Task 1
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();

            executorService.submit(new ConnectionThread(clientSocket));

            //new ConnectionThread(clientSocket).start();

            System.out.println("Connection from " + clientSocket.getRemoteSocketAddress()
                    + "; Active Connections: " + ConnectionThread.activeConnections.get());
        }
    }
}

/**
 * This thread is used for new incoming connections.
 * It is able to read request headers, greet a GET request, echo the body of a POST request and send a file following a
 * Get request with a Range header field. Furthermore, a timout is implemented.
 */
class ConnectionThread extends Thread {
    static AtomicInteger activeConnections = new AtomicInteger();

    Socket clientSocket;
    BufferedReader in;
    DataOutputStream out;

    // TODO: Add necessary fields if needed.

    Map<String, String> requestHeaders = new HashMap<>();

    byte[] responseBody;
    String contentType = "text/plain";

    ConnectionThread(Socket socket) {

        clientSocket = socket;
    }

    @Override
    public void run() {
        activeConnections.incrementAndGet();

        try {
            // TASK 3
            clientSocket.setSoTimeout(10000);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());

            // read method and query
            String[] request = in.readLine().split(" ");
            String method = request[0];
            String query = request[1];

            // process headers
            try {
                readHeaders();
            } catch (DataFormatException e) {
                // invalid headers
                e.printStackTrace();
                sendResponse(400);
            } catch (SocketException e) {
                // timeout
                e.printStackTrace();
                sendResponse(408);
            }

            // process request according to method
            if (method.equals("GET")) {
                if (query.equals("/")) {
                    responseBody = "Hello, World!".getBytes();
                    sendResponse(200);
                } else {
                    try {
                        responseBody = fetchResource(query);
                        if (requestHeaders.containsKey("Range")) {
                            sendResponse(206);
                        } else {
                            sendResponse(200);
                        }
                    } catch (FileNotFoundException e) {
                        // faulty query or file path
                        e.printStackTrace();
                        sendResponse(404);
                    } catch (NumberFormatException e) {
                        // Parsing error
                        e.printStackTrace();
                        sendResponse(400);
                    }
                }
            } else if (method.equals("POST")) {
                try {
                    responseBody = ("Hello, RUDY!\r\n" + readBody()).getBytes();
                } catch (DataFormatException e) {
                    sendResponse(400);
                }
                sendResponse(200);
            } else {
                sendResponse(405);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            activeConnections.decrementAndGet();
        }
    }

    /**
     * Retrieves data from a file at the given location.
     *
     * @param query file location
     * @return String with all the data from the requested ranges.
     * @throws IOException if file was not found or there was an IO problem
     */
    private byte[] fetchResource(String query) throws IOException {
        File file;
        byte[] response;
        file = new File("resources" + query);
        RandomAccessFile RAfile = new RandomAccessFile(file, "r");

        if (requestHeaders.containsKey("Range")) {
            contentType = "multipart/byteranges";
            // extract ranges from header
            String rangeHeaderValue = requestHeaders.get("Range");
            String ranges = rangeHeaderValue.split("=")[1];

            String[] tokens = ranges.split(",");
            byte[][] multipleRangeData = new byte[tokens.length][];


            int responseBodyLength = 0;

            // extract and save data for every range in multipleRangeData
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = tokens[i].trim();

                String left = tokens[i].split("-")[0];
                String right = tokens[i].split("-")[1];
                long start = left.equals("") ? 0 : Long.parseLong(left);
                long end = right.equals("") ? RAfile.length() : Long.parseLong(right);
                end = Math.min(end, RAfile.length());

                int length = (int) (end - start);
                responseBodyLength += length;
                RAfile.seek(start);
                multipleRangeData[i] = new byte[length];
                RAfile.read(multipleRangeData[i]);
            }
            RAfile.close();

            // build response body
            response = new byte[responseBodyLength];
            int index = 0;
            for (byte[] rangeData : multipleRangeData) {
                for (byte b : rangeData) {
                    response[index++] = b;
                }
            }
        } else {
            contentType = "image/png";
            response = new byte[(int) RAfile.length()];
            RAfile.seek(0);
            RAfile.read(response);
            RAfile.close();
        }

        return response;
    }

    /**
     * Reads headers and saves them in map, returns when empty line is sent.
     * This method is blocking while receiving data.
     *
     * @throws SocketException     if in.readLine() times out
     * @throws DataFormatException if headerLine was not valid
     * @throws IOException         if there was a problem with the socket
     */
    private void readHeaders() throws DataFormatException, IOException {
        // try to read header lines, this throws a SocketException if nothing is read for specified period
        String headerLine;
        while ((headerLine = in.readLine()) != null) {
            // check for empty line -> header is finished
            if (headerLine.isEmpty()) return;

            // split header line at first ':'
            String[] header = headerLine.split(":", 2);
            if (header.length == 2) {
                requestHeaders.put(header[0].strip(), header[1].strip());
            } else {
                // invalid header line
                throw new DataFormatException("Invalid header line was sent.");
            }
        }
    }

    /**
     * Reads the body of a request.
     * This method is blocking while waiting for data.
     *
     * @return body of request
     * @throws DataFormatException if body length was not the same as Content-Length
     */
    private String readBody() throws DataFormatException, IOException {
        StringBuilder body = new StringBuilder();
        if (!requestHeaders.containsKey("Content-Length")) {
            throw new DataFormatException("Content-Length Header is missing.");
        }
        int contentLength = Integer.parseInt(requestHeaders.get("Content-Length"));

        // read bytes and append to body
        int x;
        while ((x = in.read()) != -1) {
            body.append((char) x);

            // check if body
            if (body.length() > contentLength) {
                throw new DataFormatException("Body is bigger than Content-Length.");
            }
        }

        return body.toString();
    }

    /**
     * Builds HTTP response according to given status code.
     *
     * @param statusCode HTTP status code
     */
    private void sendResponse(int statusCode) {
        String status = "HTTP/1.1 ";
        switch (statusCode) {
            case 200:
                status += "200 OK";
                break;
            case 206:
                status += "206 Partial Content";
                break;
            case 400:
                status += "400 Bad Request";
                break;
            case 405:
                status += "405 Method Not Allowed";
                break;
            case 408:
                status += "408 Request Timeout";
                break;
            default:
                status += "501 Not Implemented";
        }
        status += "\r\n";

        try {
            //send response
            out.writeBytes(status);
            out.writeBytes("Content-Type: " + contentType + "\r\n");
            out.writeBytes("Content-Length: " + responseBody.length + "\r\n");
            out.writeBytes("Connection: close\r\n");
            out.writeBytes("\r\n");
            if (statusCode == 200 || statusCode == 206) {
                out.write(responseBody);
                out.write("\r\n".getBytes());
            }
            // close connection
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}