package org.categories;

import java.io.IOException;
import java.net.Socket;

public class Connector {

    Socket socket;
    Connector() {
    }
    public void connect(String host, Integer port) {
        try {
            socket = new Socket(host, port);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void send(String message) throws IOException {
        socket.getOutputStream().write(message.getBytes());
    }

    public String receive() throws IOException {
        byte[] buffer = new byte[1024];
        int read = socket.getInputStream().read(buffer);
        return new String(buffer, 0, read);
    }

    public void close() throws IOException {
        socket.close();
    }
}
