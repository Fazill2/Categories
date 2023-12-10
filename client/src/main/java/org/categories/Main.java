package org.categories;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

public class Main {
    static boolean proceedDirectlyToResults = false;
    static boolean proceedDirectlyToGame = true;

    public static void main(String[] args) {
        Config config = new Config();
        Properties properties = config.readConfigFile("config.properties");
        String host = properties.getProperty("host", "localhost");
        int port = Integer.parseInt(properties.getProperty("port", "2100"));

        Connector connector = new Connector();
        GameFrame gameFrame = new GameFrame(500,500);

        connectToLocalhost(gameFrame, connector, host, port);
        loginActionListener(gameFrame, connector);
        sendAnswerActionListener(gameFrame, connector);
        lobbyReadyActionListener(gameFrame, connector);

        closeWindowListener(gameFrame);
    }

    private static void closeWindowListener(GameFrame gameFrame) {
        gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(gameFrame,
                        "Are you sure you want to close this window?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
                    System.exit(0);
                }
            }
        });
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
                            break;
                        } catch (Exception e) {
                            gameFrame.error("Connection error.");
                        }
                    }
                }
        );
    }

    private static void sendAnswerActionListener(GameFrame gameFrame, Connector connector) {
        gameFrame.submitButton.addActionListener(e -> {
            try {
                String msg = "ANS:" + getUserInput(gameFrame);
                connector.send(msg);
            } catch (IOException ex) {
                gameFrame.error(ex.getMessage());
            }
        });
    }



    private static void loginActionListener(GameFrame gameFrame, Connector connector) {
        gameFrame.loginButton.addActionListener(e -> {
            try {
                String msg = "LOGIN:" + getText(gameFrame);
                connector.send(msg);
                String response = connector.receive().trim();
                if (response.equals("OK")) {
                    System.out.println(response);
                    gameFrame.createLobbyPanel(getText(gameFrame));
                    gameFrame.setContentPane(gameFrame.lobbyPanel);
                    gameFrame.validate();
                } else {
                    System.out.println(response);
                    gameFrame.error("Login was taken");
                }
            } catch (IOException | InterruptedException ex) {
                gameFrame.error(ex.getMessage());
            }
        });
    }

    private static void lobbyReadyActionListener(GameFrame gameFrame, Connector connector) {
        gameFrame.lobbyReadyButton.addActionListener(e -> {
            try {
                String msg = "READY";
                connector.send(msg);
                if (proceedDirectlyToResults){
                    gameFrame.createGameResultsPanel();
                    gameFrame.setContentPane(gameFrame.resultsPanel);
                    gameFrame.validate();
                } else if (proceedDirectlyToGame){
                    gameFrame.createGamePanel();
                    gameFrame.setContentPane(gameFrame.gamePanel);
                    gameFrame.validate();
                }
                else {
                    gameFrame.createWaitingPanel();
                    gameFrame.setContentPane(gameFrame.waitingPanel);
                    gameFrame.validate();
                }

            } catch (IOException ex) {
                gameFrame.error(ex.getMessage());
            }
        });
    }

    private static String getText(GameFrame gameFrame) {
        return gameFrame.textField.getText();
    }

    private static String getUserInput(GameFrame gameFrame) {
        return gameFrame.gameTextField.getText();
    }
}