package org.categories;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Connector connector = new Connector();
        GameFrame gameFrame = new GameFrame(400, 400);
        gameFrame.useLoginPanel();
        gameFrame.setVisible(true);
        while (true) {
            try {
                connector.connect("localhost", 2100);
                // gameFrame.connectionSuccess();
                break;
            } catch (Exception e) {
                gameFrame.connectionError();
            }
        }
        gameFrame.button.addActionListener(new ActionListener() {
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
    }
}