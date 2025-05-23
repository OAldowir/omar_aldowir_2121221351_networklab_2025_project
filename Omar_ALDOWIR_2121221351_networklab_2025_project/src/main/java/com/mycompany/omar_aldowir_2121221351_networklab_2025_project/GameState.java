/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author omaraldowir
 */
public class GameState implements Serializable {

    private Player player1;
    private Player player2;
    private Player currentTurn;
    private List<Region> regions;

    public GameState(Player p1, Player p2) {
        this.player1 = p1;
        this.player2 = p2;
        this.currentTurn = p1;
        this.regions = new ArrayList<>();

        
        for (char c = 'A'; c <= 'F'; c++) {
            regions.add(new Region(String.valueOf(c)));
        }

        assignRegionsRandomly();
    }

    private void assignRegionsRandomly() {
        Collections.shuffle(regions);
        for (int i = 0; i < 3; i++) {
            player1.addRegion(regions.get(i));
        }
        for (int i = 3; i < 6; i++) {
            player2.addRegion(regions.get(i));
        }
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public void endTurn() {
        currentTurn.resetReinforcements();
        currentTurn = (currentTurn == player1) ? player2 : player1;
    }

    public boolean isGameOver() {
        return player1.getRegions().size() == 6 || player2.getRegions().size() == 6;
    }

    public Player getWinner() {
        if (player1.getRegions().size() == 6) {
            return player1;
        }
        if (player2.getRegions().size() == 6) {
            return player2;
        }
        return null;
    }

    public List<Region> getRegions() {
        return regions;
    }

    // attack 
    public String attack(Region from, Region to) {
        if (from.getOwner() != currentTurn) {
            return "cant attack from a region you dont own.";
        }

        if (to.getOwner() == currentTurn) {
            return "can attack a region that you own.";
        }

        if (!from.canAttack()) {
            return "cant attack from a region that contains less than 2 soldiers.";
        }

        Random rand = new Random();
        int attackerRoll = rand.nextInt(6) + 1;
        int defenderRoll = rand.nextInt(6) + 1;

        StringBuilder result = new StringBuilder();
        result.append("attacker tossed: ").append(attackerRoll).append("\n");
        result.append("defender tossed: ").append(defenderRoll).append("\n");

        if (attackerRoll > defenderRoll) {
            to.removeSoldiers(1);
            result.append("defender lost a unit!\n");

            
            if (to.getSoldiers() == 0) {
                Player defender = to.getOwner();
                defender.removeRegion(to);
                currentTurn.addRegion(to);
                
                from.removeSoldiers(1);
                to.addSoldiers(1);


                result.append("The region ").append(to.getName()).append(" has been captured!\n");

            }
        } else {
            from.removeSoldiers(1);
            result.append("attacker lost a unit!\n");
        }

        return result.toString();
    }

    
    //move 
    public String moveTroops(Region from, Region to, int count) {
        if (from.getOwner() != currentTurn || to.getOwner() != currentTurn) {
            return "You can only move soldiers between regions you own.";
        }

        if (count <= 0) {
            return "moved soliders number should be more that a zero.";
        }

        if (from.getSoldiers() - count < 1) {
            return "at least a solider should be in a region , region should not be empty";

        }

        from.removeSoldiers(count);
        to.addSoldiers(count);

        return "Moved " + count + " soldier(s) from " + from.getName() + " to " + to.getName() + ".";
    }
//move

    public Player checkWinner() {
        Player candidate = regions.get(0).getOwner();
        for (Region region : regions) {
            if (region.getOwner() != candidate) {
                return null; 
            }
        }
        return candidate; 
    }

}
