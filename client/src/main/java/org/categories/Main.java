package org.categories;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;

public class Main {
    static int round;
    public static Connector connector = new Connector();

    static GameFrame gameFrame = new GameFrame(500,500);

    public static void main(String[] args) {
        Config config = new Config();
        Properties properties = config.readConfigFile("config.properties");
        String host = properties.getProperty("host", "localhost");
        int port = Integer.parseInt(properties.getProperty("port", "2100"));

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
                            startReceivingThread();
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
            } catch (IOException ex) {
                gameFrame.error(ex.getMessage());
            }
        });
    }

    private static void lobbyReadyActionListener(GameFrame gameFrame, Connector connector) {
        gameFrame.lobbyReadyButton.addActionListener(e -> {
            try {
                String msg = "ACTIVE:OK";
                connector.send(msg);
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

    private static void startReceivingThread() {
        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    String response = connector.receiveMessage().trim();
                    handleResponse(response);
                }
            } catch (IOException e) {
                throw new IllegalStateException("Issue with receiving messages", e);
            }
        });
        receiveThread.start();
    }

    private static void handleResponse(String response){
        if (response.equals("OK")){
            System.out.println(response);
            gameFrame.createLobbyPanel(getText(gameFrame));
            gameFrame.setContentPane(gameFrame.lobbyPanel);
            gameFrame.validate();
        } else if (response.equals("NO")) {
            System.out.println(response);
            gameFrame.error("Login was taken");
        } else if (response.equals("WAIT")){
            gameFrame.createWaitingPanel();
            gameFrame.setContentPane(gameFrame.waitingPanel);
            gameFrame.validate();
        } else if (response.equals("ENDGAME")){
            gameFrame.createGameResultsPanel();
            gameFrame.setContentPane(gameFrame.resultsPanel);
            gameFrame.validate();
        } else if (response.startsWith("ROUND")){
            String [] msg = response.split(":");
            gameFrame.createGamePanel();
            round = Integer.parseInt(msg[1]);
            gameFrame.letterLabel.setText("Current letter: " + msg[2]);
            String category = (Integer.parseInt(msg[3]) == 0) ? "Countries" : "Cities";
            gameFrame.categoryLabel.setText("Current category: " + category);

            timer();

            gameFrame.setContentPane(gameFrame.gamePanel);
            gameFrame.validate();
        }

    }

    private static void timer() {
        int initialTime = 14;
        int countdownInterval = 1000;

        Timer countdownTimer = new Timer(countdownInterval, new ActionListener() {
            int timeLeft = initialTime;

            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.timer.setValue(timeLeft);
                if (timeLeft == 1) {
                    ((Timer) e.getSource()).stop();
                } else {
                    timeLeft--;
                }
            }
        });

        countdownTimer.start();
    }
}