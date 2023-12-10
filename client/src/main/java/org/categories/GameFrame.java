package org.categories;

import javax.swing.*;

public class GameFrame extends JFrame {
    String username;

    JTextField textField;
    JTextField gameTextField;

    JButton submitButton = new JButton("Submit Answer");
    JButton loginButton = new JButton("Submit Name");
    JButton lobbyReadyButton = new JButton("Yes");

    JLabel lobbyLabel;
    JLabel waitingLabel;
    JLabel basicLabel;
    JLabel categoryLabel;
    JLabel letterLabel;
    JLabel totalUsers;

    JPanel gamePanel;
    JPanel waitingPanel;
    JPanel loginPanel;
    JPanel lobbyPanel;
    JPanel resultsPanel;

    JTable resultsTable;

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

    public void createLobbyPanel(String user) {
        username = user;
        lobbyPanel = new JPanel();
        lobbyLabel = new JLabel(username+", are you ready to play?");
        lobbyPanel.add(lobbyLabel);
        lobbyPanel.add(lobbyReadyButton);
    }

    public void createWaitingPanel() {
        waitingPanel = new JPanel();
        waitingLabel = new JLabel("Waiting for players..");
        totalUsers = new JLabel("Currently 1/4 players are ready.");

        waitingPanel.add(waitingLabel);
        waitingPanel.add(totalUsers);
    }

    public void createGameResultsPanel() {
        resultsPanel = new JPanel();

        String[][] data = {
                { "1", "Janir", "2137" },
                { "2", "Faziil", "213" },
                { "3", "JuanPabloII", "21" },
                { "4", "Juan Pablo III", "2" }
        };

        // Column Names
        String[] columnNames = { "Position", "Username", "Points" };

        // Initializing the JTable
        resultsTable = new JTable(data, columnNames);
        resultsTable.setBounds(0, 0, 30, 20);

        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(resultsTable);
        resultsPanel.add(sp);
    }

    public void error(String message){
        JOptionPane.showMessageDialog(null, message);
    }
}
