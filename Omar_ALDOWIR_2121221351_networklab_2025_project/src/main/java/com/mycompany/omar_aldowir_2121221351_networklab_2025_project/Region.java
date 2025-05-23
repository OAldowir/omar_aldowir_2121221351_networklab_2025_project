/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.omar_aldowir_2121221351_networklab_2025_project;

import java.io.Serializable;

/**
 *
 * @author omaraldowir
 */
public class Region implements Serializable {
    private String name;
    private Player owner;
    private int soldiers;

    public Region(String name) {
        this.name = name;
        this.soldiers = 3; 
    }

    public String getName() { return name; }

    public Player getOwner() { return owner; }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getSoldiers() {
        return soldiers;
    }

    public void addSoldiers(int count) {
        soldiers += count;
    }

    public void removeSoldiers(int count) {
        soldiers = Math.max(soldiers - count, 0);
    }

    public boolean canAttack() {
        return soldiers >= 2;
    }
}

