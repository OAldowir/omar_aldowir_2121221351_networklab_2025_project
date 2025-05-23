/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author omaraldowir
 */
public class Player implements Serializable {
    private String name;
    private List<Region> regions;
    private int reinforcementsLeft = 3;

    public Player(String name) {
        this.name = name;
        this.regions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public List<Region> getRegions() {
        return regions;
    }

    public void addRegion(Region r) {
        regions.add(r);
        r.setOwner(this);
    }

    public void removeRegion(Region r) {
        regions.remove(r);
    }

    public int getReinforcementsLeft() {
        return reinforcementsLeft;
    }

    public void resetReinforcements() {
        reinforcementsLeft = 3;
    }
    

    public boolean canReinforce() {
        return reinforcementsLeft > 0;
    }

    public void useReinforcement() {
        if (reinforcementsLeft > 0)
            reinforcementsLeft--;
    }
    
}
