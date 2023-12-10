package org.categories;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

public class Main extends Connector {
    public static void main(String[] args) {
        Config config = new Config();
        Properties properties = config.readConfigFile("config.properties");
        String host = properties.getProperty("host", "localhost");
        int port = Integer.parseInt(properties.getProperty("port", "2100"));

        Connector connector = new Connector();
        GameFrame gameFrame = new GameFrame(400, 400);

        login(gameFrame, connector);
        sendAnswer(gameFrame, connector);
        connectToLocalhost(gameFrame, connector, host, port);
    }

    private static void connectToLocalhost(GameFrame gameFrame, Connector connector, String host, int port) {
        SwingUtilities.invokeLater(
                () -> {
                    gameFrame.setVisible(true);
                    gameFrame.createLoginPanel();
                    gameFrame.setContentPane(gameFrame.loginPanel);
                    gameFrame.validate();
                    while (true) {
                        try {
                            connector.connect(host, port);
                            // gameFrame.connectionSuccess();
                            break;
                        } catch (Exception e) {
                            gameFrame.connectionError();
                        }
                    }
                }
        );
    }

    private static void sendAnswer(GameFrame gameFrame, Connector connector) {
        gameFrame.submitButton.addActionListener(e -> {
            try {
                String msg = "ANS:" + gameFrame.gameTextField.getText();
                String length = msg.length() > 9 ? "" + msg.length() : "0" + msg.length();
                msg = length + msg;
                connector.send(msg);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private static void login(GameFrame gameFrame, Connector connector) {
        gameFrame.loginButton.addActionListener(e -> {
            try {
                String msg = "LOGIN:" + gameFrame.textField.getText();
                String length = msg.length() > 9 ? "" + msg.length() : "0" + msg.length();
                msg = length + msg;
                System.out.println(msg);
                connector.send(msg);
                String response = connector.receive();
                if (response.trim().equals("OK")) {
                    System.out.println(response);
                    gameFrame.createGamePanel();
                    gameFrame.setContentPane(gameFrame.gamePanel);
                    gameFrame.validate();
                } else {
                    System.out.println(response);
                    gameFrame.loginTaken();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}