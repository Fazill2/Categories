package org.categories;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GameFrame extends JFrame {
    String username;
    GameParams gameParams = new GameParams();

    JTextField textField;
    JTextField gameTextField;
    JLabel pointsLabel = new JLabel("Points: 0");
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

    JProgressBar timer;

    JTable resultsTable;
    DefaultTableModel model;

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
        gameTextField = new JTextField(26);
        timer = new JProgressBar(0,gameParams.getRoundTime());
        timer.setValue(gameParams.getRoundTime());
        gamePanel.add(categoryLabel);
        gamePanel.add(letterLabel);
        gamePanel.add(gameTextField);
        gamePanel.add(submitButton);
        gamePanel.add(timer);
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
        };

        // Column Names
        String[] columnNames = {"Username", "Points" };
        model = new DefaultTableModel(data, columnNames);
        // Initializing the JTable
        resultsTable = new JTable(model);
        resultsTable.setBounds(0, 0, 30, 20);
        // adding it to JScrollPane
        JScrollPane sp = new JScrollPane(resultsTable);
        resultsPanel.add(sp);
    }

    public void error(String message){
        JOptionPane.showMessageDialog(null, message);
    }
}
