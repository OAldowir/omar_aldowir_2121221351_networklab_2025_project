/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author omaraldowir
 */
public class GameClient {

    private static final String SERVER_IP = "localhost"; 
    private static final int SERVER_PORT = 5050;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static void main(String[] args) {
        new GameClient().startClient();
    }

    public void startClient() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            System.out.println("connection is done.");

            new Thread(this::listenForUpdates).start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print(" write an order (ex: attack A B or reinforce or end ): ");
                String input = scanner.nextLine();
                out.writeObject(input);
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("error with connecting with the server.");
            e.printStackTrace();
        }
    }

    private void listenForUpdates() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof GameState) {
                    GameState game = (GameState) obj;

                    System.out.println("\n--- new update from server ---");
                    for (Region r : game.getRegions()) {
                        System.out.println("region " + r.getName() + " is for " +
                            r.getOwner().getName() + " - soliders : " + r.getSoldiers());
                    }
                    System.out.println(" current turn: " + game.getCurrentTurn().getName());
                    System.out.println("------------------------------\n");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("lost connection with server.");
        }
    }
}
