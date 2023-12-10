package org.categories;

import java.io.IOException;
import java.net.Socket;

public class Connector {

    private Socket socket;
    private String receivedMessage;

    public Connector() {
    }

    public void connect(String host, Integer port) {
        try {
            socket = new Socket(host, port);
            startReceivingThread();
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

    public synchronized String receive() throws IOException, InterruptedException {
        while (receivedMessage == null) {
            wait();
        }
        String message = receivedMessage;
        receivedMessage = null;
        return message;
    }

    private void startReceivingThread() {
        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    String response = receiveMessage();
                    synchronized (Connector.this) {
                        receivedMessage = response;
                        notify();
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("Issue with receiving messages", e);
            }
        });
        receiveThread.start();
    }

    private String receiveMessage() throws IOException {
        byte[] buffer = new byte[1024];
        int read = socket.getInputStream().read(buffer);
        return new String(buffer, 0, read);
    }

//    public void close() throws IOException {
//        socket.close();
//    }
}
