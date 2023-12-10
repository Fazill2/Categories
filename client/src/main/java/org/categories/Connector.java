package org.categories;

import java.io.IOException;
import java.net.Socket;

public class Connector {

    private Socket socket;

    public Connector() {
    }

    public void connect(String host, Integer port) {
        try {
            socket = new Socket(host, port);
            // startReceivingThread();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void send(String msg) throws IOException {
        String length = msg.length() > 9 ? "" + msg.length() : "0" + msg.length();
        msg = length + msg;
        System.out.println(msg);
        socket.getOutputStream().write(msg.getBytes());
    }

    String receiveMessage() throws IOException {
        byte[] lengthBuffer = new byte[2];
        int read = socket.getInputStream().read(lengthBuffer, 0, 2);
        if (read == -1) {
            throw new IllegalStateException("Connection closed");
        }
        int length = Integer.parseInt(new String(lengthBuffer));
        byte[] buffer = new byte[length];
        read = socket.getInputStream().read(buffer, 0, length);
        if (read != length) {
            throw new IllegalStateException("Connection closed");
        }
        return new String(buffer, 0, read);
    }
}
