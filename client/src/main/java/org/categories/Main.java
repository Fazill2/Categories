package org.categories;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        // read ip and port from config file

        Connector connector = new Connector();
        GameFrame gameFrame = new GameFrame(400, 400);

        gameFrame.loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connector.send(gameFrame.textField.getText());
                    String response = connector.receive();
                    if (response.trim().equals("OK")) {
                        gameFrame.useGamePanel();
                    } else {
                        System.out.println(response);
                        gameFrame.textField.setText(response);
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        gameFrame.submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    connector.send(gameFrame.textField.getText());
                    String response = connector.receive();
                    gameFrame.textField.setText(response);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        gameFrame.setVisible(true);
                        gameFrame.createLoginPanel();
                        gameFrame.createGamePanel();
                        gameFrame.setContentPane(gameFrame.loginPanel);
                        gameFrame.setContentPane(gameFrame.gamePanel);
                        gameFrame.useLoginPanel();
                        while (true) {
                            try {
                                connector.connect("localhost", 2100);
                                // gameFrame.connectionSuccess();
                                break;
                            } catch (Exception e) {
                                gameFrame.connectionError();
                            }
                        }
                    }
                }
        );
    }
}