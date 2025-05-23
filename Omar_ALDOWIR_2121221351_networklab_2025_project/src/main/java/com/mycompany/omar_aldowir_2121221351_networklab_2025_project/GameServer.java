package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

    private static final int PORT = 5050;

    private static Socket player1Socket;
    private static Socket player2Socket;

    private static ObjectOutputStream p1Out;
    private static ObjectOutputStream p2Out;

    private static ObjectInputStream p1In;
    private static ObjectInputStream p2In;

    private static GameState gameState;

    private static Map<Player, ObjectOutputStream> outputs = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("server is online on port :" + PORT + "\n waiting for players to join");

           
            player1Socket = serverSocket.accept();
            System.out.println("Player 1 connected.");
            p1Out = new ObjectOutputStream(player1Socket.getOutputStream());
            p1In = new ObjectInputStream(player1Socket.getInputStream());

            player2Socket = serverSocket.accept();
            System.out.println("Player 2 connected.");
            p2Out = new ObjectOutputStream(player2Socket.getOutputStream());
            p2In = new ObjectInputStream(player2Socket.getInputStream());

            
            Player p1 = new Player("Player 1");
            Player p2 = new Player("Player 2");
            gameState = new GameState(p1, p2);

            
            outputs.put(p1, p1Out);
            outputs.put(p2, p2Out);

            
            broadcastGameState();

          
            new Thread(() -> handlePlayer(p1In, p1)).start();
            new Thread(() -> handlePlayer(p2In, p2)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handlePlayer(ObjectInputStream in, Player player) {
        try {
            while (true) {

                Object obj = in.readObject();
                if (obj instanceof String) {
                    String command = ((String) obj).trim();

                    System.out.println(player.getName() + " send: " + command);

                    if (command.startsWith("attack")) {
                        String[] parts = command.split(" ");
                        Region from = findRegionByName(parts[1]);
                        Region to = findRegionByName(parts[2]);

                        if (from == null || to == null) {
                            System.out.println("one of the regions is not existed.");
                            continue;
                        }

                        String result = gameState.attack(from, to);
                        System.out.println(" attack result : " + result);

                        
                        outputs.get(player).writeObject("RESULT:" + result);
                    } else if (command.startsWith("reinforce")) {
                        String[] parts = command.split(" ");
                        Region region = findRegionByName(parts[1]);

                        if (region == null) {
                            System.out.println("region is not existed.");
                            continue;
                        }

                        if (region.getOwner() != player) {

                            System.out.println("cant reinforce a region that you dont own");
                            continue;
                        }

                        if (!player.canReinforce()) {

                            System.out.println("reinforce chances are done for this turn");
                            continue;
                        }

                        region.addSoldiers(1);
                        player.useReinforcement();
                        System.out.println("region been reinforced " + region.getName() + ". now contains " + region.getSoldiers() + " soldiers.");
                    } else if (command.startsWith("move")) {
                        String[] parts = command.split(" ");
                        if (parts.length < 4) {
                            continue;
                        }

                        Region from = findRegionByName(parts[1]);
                        Region to = findRegionByName(parts[2]);
                        int count;

                        try {
                            count = Integer.parseInt(parts[3]);
                        } catch (NumberFormatException e) {

                            System.out.println("soldiers count is not valide");
                            continue;
                        }

                        if (from == null || to == null) {

                            System.out.println("one of the regions is not existed");
                            continue;
                        }

                        if (from.getOwner() != player || to.getOwner() != player) {

                            System.out.println("soldiers moves are only between regions that you own.");
                            continue;
                        }

                        if (count <= 0 || from.getSoldiers() <= count) {
                     
                            System.out.println("at least one soldier should be in the region ,not enough soldiers. ");
                            continue;
                        }

                        from.removeSoldiers(count);
                        to.addSoldiers(count);

                        System.out.println("Moved " + count + " soldier(s) from " + from.getName() + " to " + to.getName());
                    } else if (command.equalsIgnoreCase("end")) {
                        gameState.endTurn();
                        System.out.println("turn is done. now turn" + gameState.getCurrentTurn().getName());
                    }

                  
                    broadcastGameState();

                  
                    Player winner = gameState.checkWinner();

                    if (winner != null) {
                        System.out.println("🏆 The winner is: " + winner.getName());

                        // Send popup message to both players
                        for (ObjectOutputStream out : outputs.values()) {
                            try {
                                out.writeObject("RESULT:🎉 Game Over! " + winner.getName() + " has won the game!");
                            } catch (IOException e) {
                                System.out.println("Failed to send winner message.");
                            }
                        }

                        
                        System.exit(0);
                    }

                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Lost connection with " + player.getName());
        }
    }

    private static Region findRegionByName(String name) {
        for (Region r : gameState.getRegions()) {
            if (r.getName().equalsIgnoreCase(name)) {
                return r;
            }
        }
        return null;
    }

    private static void broadcastGameState() {
        try {
            p1Out.reset();
            p2Out.reset();
            p1Out.writeObject(gameState);
            p2Out.writeObject(gameState);
            p1Out.flush();
            p2Out.flush();
        } catch (IOException e) {
            System.out.println("Error sending game state.");
        }
    }

    private static void broadcastMessage(String message) {
        for (ObjectOutputStream out : outputs.values()) {
            try {
                out.writeObject("--- Notification ---\n" + message + "\n------------------------------");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
