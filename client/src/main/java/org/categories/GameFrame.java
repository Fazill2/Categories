package org.categories;

import javax.swing.*;

public class GameFrame extends JFrame {
    JTextField textField;
    JTextField gameTextField;
    JButton submitButton = new JButton("Submit Answer");
    JButton loginButton = new JButton("Submit Name");
    JLabel basicLabel;
    JLabel categoryLabel;
    JLabel letterLabel;
    JPanel gamePanel;
    JPanel loginPanel;
    public GameFrame(int width, int height){
        super("Categories");
        this.setSize(width, height);
        this.setLocation(50,50);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public void createGamePanel() {
        gamePanel = new JPanel();
        categoryLabel = new JLabel("Current category: ");
        letterLabel = new JLabel("Current letter: ");
        gameTextField = new JTextField("enter your answer", 26);
        gamePanel.add(categoryLabel);
        gamePanel.add(letterLabel);
        gamePanel.add(gameTextField);
        gamePanel.add(submitButton);
    }

    public void createLoginPanel() {
        loginPanel = new JPanel();
        basicLabel = new JLabel("Enter your name: ");
        textField = new JTextField("enter your name", 16);
        loginPanel.add(basicLabel);
        loginPanel.add(textField);
        loginPanel.add(loginButton);
    }

    public void useGamePanel(){
        this.loginPanel.setVisible(false);
        this.gamePanel.setVisible(true);
    }

    public void useLoginPanel(){
        this.loginPanel.setVisible(true);
        this.gamePanel.setVisible(false);
    }

    public void connectionError(){
        JOptionPane.showMessageDialog(null, "Connection error");
    }
}
