package org.categories;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Locale;

public class GameFrame extends JFrame {
    JTextField textField;
    JButton button;
    JLabel basicLabel;
    JLabel categoryLabel;
    JLabel letterLabel;
    JPanel gamePanel;
    JPanel loginPanel;
    String category;
    String letter;
    String name;
    Boolean loggedIn = false;
    Connector connector;
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
        textField = new JTextField("enter your answer", 26);
        button = new JButton("Submit Answer");
        gamePanel.add(basicLabel);
        gamePanel.add(categoryLabel);
        gamePanel.add(letterLabel);
        gamePanel.add(textField);
        gamePanel.add(button);
    }

    public void createLoginPanel() {
        loginPanel = new JPanel();
        basicLabel = new JLabel("Enter your name: ");
        textField = new JTextField("enter your name", 16);
        button = new JButton("Submit Name");
        loginPanel.add(basicLabel);
        loginPanel.add(textField);
        loginPanel.add(button);
    }


    public void useGamePanel(){
        this.createGamePanel();
        this.setContentPane(gamePanel);
    }

    public void useLoginPanel(){
        this.createLoginPanel();
        this.setContentPane(loginPanel);
    }

    public void connectionError(){
        // create popup window that says "connection error"
        JOptionPane.showMessageDialog(null, "Connection error");
    }
}
