/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

import java.util.Arrays;

class Item {
    private String name;
    private int baseDamage;
    private double critChance;
    private String specialEffect;
    private int durability;
    private int value;
    private int requiredRank;
    private int upgradeLevel;
    
    public Item(String name, int baseDamage, double critChance, 
               String specialEffect, int durability, int value, int requiredRank) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.critChance = critChance;
        this.specialEffect = specialEffect;
        this.durability = durability;
        this.value = value;
        this.requiredRank = requiredRank;
        this.upgradeLevel = 0;
    }
    
    // AI-based adaptive scaling
    public void adaptToPlayer(Hunter player) {
        double rankFactor = getRankValue(player.getRank()) * 0.1;
        double levelFactor = player.getLevel() * 0.05;
        
        baseDamage += (int)(baseDamage * (rankFactor + levelFactor));
        critChance = Math.min(0.95, critChance + (levelFactor * 0.1));
    }
    
    private int getRankValue(String rank) {
        switch(rank) {
            case "E": return 1;
            case "D": return 2;
            case "C": return 3;
            case "B": return 4;
            case "A": return 5;
            case "S": return 6;
            default: return 0;
        }
    }

    public boolean canEquip(String playerRank) {
        String[] ranks = {"E", "D", "C", "B", "A", "S"};
        int playerRankIndex = Arrays.asList(ranks).indexOf(playerRank);
        return playerRankIndex >= requiredRank;
    }

    public boolean use() {
        if (durability > 0) {
            durability--;
            return true;
        }
        return false;
    }

    public void repair() {
        durability = 50;
    }

    public void upgrade() {
        baseDamage += 2;
        critChance = Math.min(0.95, critChance + 0.05);
        upgradeLevel++;
    }

    public String getName() { return name; }
    public int getBaseDamage() { return baseDamage; }
    public double getCritChance() { return critChance; }
    public String getSpecialEffect() { return specialEffect; }
    public int getDurability() { return durability; }
    public int getValue() { return value; }
    public int getRequiredRank() { return requiredRank; }
    public int getUpgradeLevel() { return upgradeLevel; }
}
