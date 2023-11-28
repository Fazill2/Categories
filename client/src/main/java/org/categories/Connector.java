package org.categories;

import javax.imageio.IIOException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connector{

    Socket socket;
    GameFrame gameFrame;
    Connector() {
    }
    public void connect(String host, Integer port) throws UnknownHostException, IOException {
        try {
            socket = new Socket(host, port);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            throw e;
        } catch (IOException e) {
            System.out.println("IO Exception");
            System.out.println(e.getMessage());
            throw e;
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
