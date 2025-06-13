/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bosakon.tobehunter;

class Skill {
    private String name;
    private boolean isActive;
    private int manaCost;
    private String effect;
    private int masteryLevel;
    private int maxMastery;
    private double situationalBoost;
    
    public Skill(String name, boolean isActive, int manaCost, String effect) {
        this.name = name;
        this.isActive = isActive;
        this.manaCost = manaCost;
        this.effect = effect;
        this.masteryLevel = 1;
        this.maxMastery = 5;
        this.situationalBoost = 0.0;
    }
    
    public void increaseMastery() {
        if (masteryLevel < maxMastery) {
            masteryLevel++;
        }
    }
    
    public double getMasteryBonus() {
        return 1 + (masteryLevel * 0.1);
    }
    
    public double getEffectiveBonus() {
        return getMasteryBonus() + situationalBoost;
    }
    
    // Context-aware effectiveness
    public void updateSituationalBoost(String context) {
        switch(context) {
            case "dungeon":
                situationalBoost = 0.15;
                break;
            case "boss":
                situationalBoost = 0.25;
                break;
            case "low_health":
                situationalBoost = 0.3;
                break;
            default:
                situationalBoost = 0.0;
        }
    }
    
    public String getName() { return name; }
    public boolean isActive() { return isActive; }
    public int getManaCost() { return manaCost; }
    public String getEffect() { return effect; }
    public int getMasteryLevel() { return masteryLevel; }
    public double getSituationalBoost() { return situationalBoost; }
}

