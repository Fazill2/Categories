package org.categories;// Connector.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Connector {

    private Socket socket;
    private BufferedReader reader;
    private String receivedMessage;

    public Connector() {
    }

    public void connect(String host, Integer port) {
        try {
            socket = new Socket(host, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            startReceivingThread();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public void send(String message) throws IOException {
        socket.getOutputStream().write(message.getBytes());
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
                throw new IllegalStateException("Issue with receiving messages",e);
            }
        });
        receiveThread.start();
    }

    private String receiveMessage() throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        int character;

        while ((character = reader.read()) != -1) {
            if (character == '\n') {
                break;
            }
            responseBuilder.append((char) character);
        }

        return responseBuilder.toString();
    }

    public void close() throws IOException {
        socket.close();
    }
}
