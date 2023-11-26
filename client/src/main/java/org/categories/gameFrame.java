package org.categories;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class gameFrame extends JFrame implements ActionListener {
    static JTextField textField;
    static JButton button;
    static JLabel basicLabel;
    static JLabel categoryLabel;
    static JLabel letterLabel;
    static JPanel gamePanel;
    static JPanel loginPanel;
    String category;
    String letter;
    public gameFrame(int width, int height, String category, String letter){
        super("Categories");
        this.category = category;
        this.letter = letter;
        createLoginPanel();
        this.add(loginPanel);
        this.setSize(width, height);
        this.setLocation(50,50);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e){
        String s = e.getActionCommand();
        if (s.equals("Submit Answer")) {
            // TODO: send answer to server
            // TODO: check if answer is valid
            System.out.println(textField.getText().trim());
            textField.setText("");
        }
        if (s.equals("Submit Name")) {
            // TODO: wait for server to accept name
            System.out.println(textField.getText().trim());
            textField.setText("");
            loginPanel.setVisible(false);
            createGamePanel(this.category, this.letter);
            this.add(gamePanel);
            gamePanel.setVisible(true);
        }
    }

    public void createGamePanel(String category, String letter) {
        gamePanel = new JPanel();
        basicLabel = new JLabel("Current category: ");
        categoryLabel = new JLabel(category);
        letterLabel = new JLabel("Current letter: " + letter);
        textField = new JTextField("enter your answer", 26);
        button = new JButton("Submit Answer");
        button.addActionListener(this);
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
        button.addActionListener(this);
        loginPanel.add(basicLabel);
        loginPanel.add(textField);
        loginPanel.add(button);
    }

    public void changeCategory(String category){
        categoryLabel.setText(category);
    }

    public void changeLetter(String letter){
        letterLabel.setText("Current letter: " + letter);
    }
}
