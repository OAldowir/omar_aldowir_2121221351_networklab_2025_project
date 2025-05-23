package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class GameClientUI extends JFrame {

    private JPanel mapPanel;
    private JLabel[] regionLabels = new JLabel[6];
    private Rectangle[] regionPositions = new Rectangle[6]; 
    private JButton attackButton;
    private JButton reinforceButton;
    private JButton moveButton;
    private JButton endTurnButton;
    private JButton cancelButton;
    private JLabel turnLabel; 

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Region selectedAttacker = null;
    private Region selectedDefender = null;
    private Region selectedReinforceRegion = null;
    private Region moveSource = null;
    private Region moveTarget = null;

    public GameClientUI() {
        setTitle("Network Risk Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        mapPanel = new JPanel(null);
        mapPanel.setBounds(0, 0, 800, 500);
        mapPanel.setBackground(new Color(245, 245, 245));
        add(mapPanel);

        
        int width = 100, height = 70;
        for (int i = 0; i < 6; i++) {
            Rectangle newBounds;
            boolean overlaps;
            do {
                int x = 100 + (int) (Math.random() * 500);
                int y = 50 + (int) (Math.random() * 350);
                newBounds = new Rectangle(x, y, width, height);
                overlaps = false;
                for (int j = 0; j < i; j++) {
                    if (regionPositions[j] != null && regionPositions[j].intersects(newBounds)) {
                        overlaps = true;
                        break;
                    }
                }
            } while (overlaps);
            regionPositions[i] = newBounds;

            JLabel label = new JLabel();
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            label.setBounds(newBounds);
            regionLabels[i] = label;
            mapPanel.add(label);
        }

        attackButton = new JButton("Attack");
        reinforceButton = new JButton("Reinforce");
        moveButton = new JButton("Move");
        endTurnButton = new JButton("End Turn");
        cancelButton = new JButton("Cancel Selection");

        cancelButton.setBounds(580, 510, 150, 30);
        add(cancelButton);
        attackButton.setBounds(100, 510, 100, 30);
        reinforceButton.setBounds(220, 510, 100, 30);
        moveButton.setBounds(340, 510, 100, 30);
        endTurnButton.setBounds(460, 510, 100, 30);

        //Attack
        add(attackButton);
        // Disable attack button until both regions are selected
        attackButton.setEnabled(false);
        attackButton.addActionListener(e -> {
            if (selectedAttacker != null && selectedDefender != null) {
                sendCommand("attack " + selectedAttacker.getName() + " " + selectedDefender.getName());
                selectedAttacker = null;
                selectedDefender = null;
                attackButton.setEnabled(false);
            }
        });
//reinforceButton
        reinforceButton.setEnabled(false);
        reinforceButton.addActionListener(e -> {
            if (selectedReinforceRegion != null) {
                sendCommand("reinforce " + selectedReinforceRegion.getName());
                selectedReinforceRegion = null;
                reinforceButton.setEnabled(false);
            }
        });

        //move
        moveButton.setEnabled(false);
        moveButton.addActionListener(e -> {
            if (moveSource != null && moveTarget != null) {
                String input = JOptionPane.showInputDialog(this, "Enter number of soldiers to move:");
                try {
                    int count = Integer.parseInt(input);
                    if (count > 0) {
                        sendCommand("move " + moveSource.getName() + " " + moveTarget.getName() + " " + count);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                moveSource = null;
                moveTarget = null;
                moveButton.setEnabled(false);
            }
        });
        cancelButton.addActionListener(e -> {
            selectedAttacker = null;
            selectedDefender = null;
            selectedReinforceRegion = null;
            moveSource = null;
            moveTarget = null;

            attackButton.setEnabled(false);
            reinforceButton.setEnabled(false);
            moveButton.setEnabled(false);

            for (JLabel label : regionLabels) {
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        });

        add(reinforceButton);
        add(moveButton);
        add(endTurnButton);
        add(cancelButton);

        // Button Actions (placeholder)
        turnLabel = new JLabel("Waiting...", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Arial", Font.BOLD, 18));
        turnLabel.setBounds(0, 0, 800, 30);
        turnLabel.setOpaque(true);
        turnLabel.setBackground(new Color(230, 230, 255));
        turnLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        add(turnLabel);


        reinforceButton.addActionListener(e -> sendCommand("reinforce A"));
        moveButton.addActionListener(e -> sendCommand("move A B 1"));
        endTurnButton.addActionListener(e -> sendCommand("end"));

        connectToServer();
    }

    private void connectToServer() {
        try {
//            Socket socket = new Socket("localhost", 5050);   
;
            Socket socket = new Socket("16.171.199.224", 5050);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());


            new Thread(() -> {
                while (true) {
                    try {
                        Object response = in.readObject();

                        if (response instanceof GameState) {
                            GameState state = (GameState) response;
                            updateUIFromState(state);
                        } 
                        else if (response instanceof String) {
                            String message = (String) response;

                            if (message.startsWith("RESULT:")) {
                                String resultText = message.substring(7);
                                JOptionPane.showMessageDialog(this, resultText, "Battle Result", JOptionPane.INFORMATION_MESSAGE);
                            } else if (message.startsWith("--- notification ---")) {
                                JOptionPane.showMessageDialog(this, message, "🎉 Game Over", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0); 
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            }).start();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private int getRegionIndex(GameState state, String name) {
        for (int i = 0; i < state.getRegions().size(); i++) {
            if (state.getRegions().get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void sendCommand(String command) {
        try {
            out.writeObject(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUIFromState(GameState state) {
        SwingUtilities.invokeLater(() -> {
            Player currentPlayer = state.getCurrentTurn();
            turnLabel.setText("Current Turn: " + state.getCurrentTurn().getName());

            for (int i = 0; i < state.getRegions().size(); i++) {
                Region region = state.getRegions().get(i);
                JLabel label = regionLabels[i];

                
                String text = "<html><center>"
                        + region.getName() + "<br>"
                        + region.getOwner().getName() + "<br>"
                        + "Soldiers: " + region.getSoldiers()
                        + "</center></html>";
                label.setText(text);
                label.setBackground(region.getOwner().getName().equals("Player 1")
                        ? new Color(200, 230, 255)
                        : new Color(255, 180, 200));

                for (MouseListener ml : label.getMouseListeners()) {
                    label.removeMouseListener(ml);
                }

                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (region.getOwner().equals(currentPlayer)) {
                            // 
                            selectedReinforceRegion = region;
                            reinforceButton.setEnabled(true);

                            // 
                            if (moveSource == null) {
                                moveSource = region;
                            } else if (moveTarget == null && region != moveSource) {
                                moveTarget = region;
                                moveButton.setEnabled(true);
                            }

                            selectedAttacker = region;
                            selectedDefender = null;
                            attackButton.setEnabled(false);

                        } else {
                            if (selectedAttacker != null) {
                                selectedDefender = region;
                                attackButton.setEnabled(true);
                            }
                        }

                        for (int j = 0; j < regionLabels.length; j++) {
                            regionLabels[j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        }

                        if (selectedReinforceRegion != null) {
                            int idx = getRegionIndex(state, selectedReinforceRegion.getName());
                            if (idx != -1) {
                                regionLabels[idx].setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                            }
                        }

                        if (moveSource != null) {
                            int idx = getRegionIndex(state, moveSource.getName());
                            if (idx != -1) {
                                regionLabels[idx].setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                            }
                        }
                        if (moveTarget != null) {
                            int idx = getRegionIndex(state, moveTarget.getName());
                            if (idx != -1) {
                                regionLabels[idx].setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 3));
                            }
                        }


                        if (selectedAttacker != null) {
                            int idx = getRegionIndex(state, selectedAttacker.getName());
                            if (idx != -1) {
                                regionLabels[idx].setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
                            }
                        }
                        if (selectedDefender != null) {
                            int idx = getRegionIndex(state, selectedDefender.getName());
                            if (idx != -1) {
                                regionLabels[idx].setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                            }
                        }
                    }
                });
            }

            mapPanel.repaint();
            mapPanel.revalidate();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameClientUI ui = new GameClientUI();
            ui.setVisible(true);
        });
    }
}
